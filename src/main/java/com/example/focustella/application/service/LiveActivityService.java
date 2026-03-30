package com.example.focustella.application.service;

import com.example.focustella.api.dto.request.LiveActivityEndRequest;
import com.example.focustella.api.dto.request.LiveActivityPauseRequest;
import com.example.focustella.api.dto.request.LiveActivityRegisterRequest;
import com.example.focustella.api.dto.request.LiveActivityResumeRequest;
import com.example.focustella.api.dto.response.LiveActivityRegisterResponse;
import com.example.focustella.api.dto.response.LiveActivityStatusResponse;
import com.example.focustella.application.port.out.LoadConstellationPort;
import com.example.focustella.application.port.out.LoadFocusSessionPort;
import com.example.focustella.common.exception.BusinessException;
import com.example.focustella.common.exception.code.FocusSessionErrorCode;
import com.example.focustella.common.exception.code.LiveActivityErrorCode;
import com.example.focustella.domain.model.Constellation;
import com.example.focustella.domain.model.ConstellationEdge;
import com.example.focustella.domain.model.ConstellationStar;
import com.example.focustella.domain.model.FocusLiveActivityStatus;
import com.example.focustella.domain.model.FocusSession;
import com.example.focustella.domain.model.FocusSessionStatus;
import com.example.focustella.infrastructure.external.ApnsClient;
import com.example.focustella.infrastructure.persistence.entity.FocusLiveActivityEntity;
import com.example.focustella.infrastructure.persistence.repository.FocusLiveActivityJpaRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
public class LiveActivityService {

    private final LoadFocusSessionPort loadFocusSessionPort;
    private final LoadConstellationPort loadConstellationPort;
    private final FocusLiveActivityJpaRepository focusLiveActivityJpaRepository;
    private final LiveActivityScheduler liveActivityScheduler;
    private final ApnsClient apnsClient;
    private final Clock clock;
    private final TransactionTemplate transactionTemplate;

    public LiveActivityService(
            LoadFocusSessionPort loadFocusSessionPort,
            LoadConstellationPort loadConstellationPort,
            FocusLiveActivityJpaRepository focusLiveActivityJpaRepository,
            LiveActivityScheduler liveActivityScheduler,
            ApnsClient apnsClient,
            Clock clock,
            PlatformTransactionManager transactionManager
    ) {
        this.loadFocusSessionPort = loadFocusSessionPort;
        this.loadConstellationPort = loadConstellationPort;
        this.focusLiveActivityJpaRepository = focusLiveActivityJpaRepository;
        this.liveActivityScheduler = liveActivityScheduler;
        this.apnsClient = apnsClient;
        this.clock = clock;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void recoverSchedules() {
        List<FocusLiveActivityEntity> runningActivities = focusLiveActivityJpaRepository.findByStatus(FocusLiveActivityStatus.RUNNING);
        log.info("Recovering live activity schedules: count={}", runningActivities.size());

        runningActivities.forEach(activity ->
                transactionTemplate.executeWithoutResult(status -> processScheduledUpdate(activity.getFocusSessionId()))
        );
    }

    @Transactional
    public LiveActivityRegisterResponse register(String userId, LiveActivityRegisterRequest request) {
        log.info(
                "Received live activity register request: focusSessionId={}, activityId={}, status={}, pushToken={}",
                request.focusSessionId(),
                request.activityId(),
                request.status(),
                maskPushToken(request.pushToken())
        );

        FocusSession focusSession = loadActiveFocusSession(userId, request.focusSessionId());
        validateRegisterRequest(focusSession, request);

        FocusLiveActivityEntity registration = focusLiveActivityJpaRepository.findByFocusSessionId(request.focusSessionId())
                .map(existing -> updateRegistration(existing, request))
                .orElseGet(() -> createRegistration(userId, request));

        focusLiveActivityJpaRepository.save(registration);
        log.info(
                "Upserted live activity push token: focusSessionId={}, status={}, activityId={}, pushToken={}",
                registration.getFocusSessionId(),
                registration.getStatus(),
                registration.getActivityId(),
                maskPushToken(registration.getPushToken())
        );

        if (registration.getStatus() == FocusLiveActivityStatus.RUNNING) {
            scheduleNextUpdate(registration, loadConstellation(registration.getConstellationId()), Instant.now(clock));
        } else {
            liveActivityScheduler.cancel(registration.getFocusSessionId());
        }

        return new LiveActivityRegisterResponse(request.focusSessionId(), true);
    }

    @Transactional
    public LiveActivityStatusResponse pause(String userId, LiveActivityPauseRequest request) {
        log.info("Received live activity pause request: focusSessionId={}, pausedAt={}", request.focusSessionId(), request.pausedAt());
        FocusLiveActivityEntity registration = loadRegistration(userId, request.focusSessionId());
        ensureRunning(registration);
        ensureFocusSessionStillActive(registration);

        liveActivityScheduler.cancel(registration.getFocusSessionId());
        registration.markPaused(request.pausedAt());

        Constellation constellation = loadConstellation(registration.getConstellationId());
        ComputedState state = computeState(registration, constellation, request.pausedAt(), false);
        sendPushAndUpdateState(registration, "update", state, request.pausedAt());
        focusLiveActivityJpaRepository.save(registration);

        return new LiveActivityStatusResponse(request.focusSessionId(), registration.getStatus());
    }

    @Transactional
    public LiveActivityStatusResponse resume(String userId, LiveActivityResumeRequest request) {
        log.info("Received live activity resume request: focusSessionId={}, resumedAt={}", request.focusSessionId(), request.resumedAt());
        FocusLiveActivityEntity registration = loadRegistration(userId, request.focusSessionId());
        ensurePaused(registration);
        ensureFocusSessionStillActive(registration);

        registration.resume(request.resumedAt());

        Constellation constellation = loadConstellation(registration.getConstellationId());
        ComputedState state = computeState(registration, constellation, request.resumedAt(), false);
        sendPushAndUpdateState(registration, "update", state, request.resumedAt());
        focusLiveActivityJpaRepository.save(registration);
        scheduleNextUpdate(registration, constellation, request.resumedAt());

        return new LiveActivityStatusResponse(request.focusSessionId(), registration.getStatus());
    }

    @Transactional
    public LiveActivityStatusResponse end(String userId, LiveActivityEndRequest request) {
        log.info(
                "Received live activity end request: focusSessionId={}, endedAt={}, reason={}",
                request.focusSessionId(),
                request.endedAt(),
                request.reason()
        );
        FocusLiveActivityEntity registration = loadRegistration(userId, request.focusSessionId());
        liveActivityScheduler.cancel(registration.getFocusSessionId());

        Constellation constellation = loadConstellation(registration.getConstellationId());
        ComputedState state = computeState(
                registration,
                constellation,
                request.endedAt(),
                request.reason() == com.example.focustella.domain.model.FocusLiveActivityEndReason.COMPLETED
        );

        registration.markEnded();
        sendPushAndUpdateState(registration, "end", state, request.endedAt());
        focusLiveActivityJpaRepository.save(registration);

        return new LiveActivityStatusResponse(request.focusSessionId(), registration.getStatus());
    }

    private void processScheduledUpdate(String focusSessionId) {
        log.info("Processing scheduled live activity update: focusSessionId={}", focusSessionId);
        Optional<FocusLiveActivityEntity> optionalRegistration = focusLiveActivityJpaRepository.findByFocusSessionId(focusSessionId);
        if (optionalRegistration.isEmpty()) {
            log.info("Skipping scheduled live activity update because registration was not found: focusSessionId={}", focusSessionId);
            return;
        }

        FocusLiveActivityEntity registration = optionalRegistration.get();
        if (registration.getStatus() != FocusLiveActivityStatus.RUNNING) {
            log.info(
                    "Skipping scheduled live activity update because registration is not running: focusSessionId={}, status={}",
                    focusSessionId,
                    registration.getStatus()
            );
            liveActivityScheduler.cancel(focusSessionId);
            return;
        }

        Optional<FocusSession> focusSession = loadFocusSessionPort.loadByIdAndUserId(registration.getFocusSessionId(), registration.getUserId());
        if (focusSession.isEmpty() || focusSession.get().status() == FocusSessionStatus.COMPLETED) {
            registration.markEnded();
            focusLiveActivityJpaRepository.save(registration);
            liveActivityScheduler.cancel(focusSessionId);
            log.info("Stopped live activity scheduling because focus session is already completed: focusSessionId={}", focusSessionId);
            return;
        }

        Constellation constellation = loadConstellation(registration.getConstellationId());
        Instant now = Instant.now(clock);
        ComputedState state = computeState(registration, constellation, now, false);
        int lastPushedCount = registration.getLastPushedDiscoveredCount() == null ? -1 : registration.getLastPushedDiscoveredCount();

        if (state.discoveredStarCount() > lastPushedCount) {
            sendPushAndUpdateState(registration, "update", state, now);
            focusLiveActivityJpaRepository.save(registration);
        } else {
            log.info(
                    "No live activity push sent because discovered star count did not advance: focusSessionId={}, discoveredStarCount={}, lastPushedDiscoveredCount={}",
                    focusSessionId,
                    state.discoveredStarCount(),
                    lastPushedCount
            );
        }

        if (registration.getStatus() == FocusLiveActivityStatus.RUNNING) {
            scheduleNextUpdate(registration, constellation, now);
        }
    }

    private FocusSession loadActiveFocusSession(String userId, String focusSessionId) {
        FocusSession focusSession = loadFocusSessionPort.loadByIdAndUserId(focusSessionId, userId)
                .orElseThrow(() -> new BusinessException(FocusSessionErrorCode.SESSION_NOT_FOUND));

        if (focusSession.status() == FocusSessionStatus.COMPLETED) {
            throw new BusinessException(LiveActivityErrorCode.SESSION_ALREADY_COMPLETED);
        }
        return focusSession;
    }

    private void ensureFocusSessionStillActive(FocusLiveActivityEntity registration) {
        FocusSession focusSession = loadFocusSessionPort.loadByIdAndUserId(registration.getFocusSessionId(), registration.getUserId())
                .orElseThrow(() -> new BusinessException(FocusSessionErrorCode.SESSION_NOT_FOUND));

        if (focusSession.status() == FocusSessionStatus.COMPLETED) {
            throw new BusinessException(LiveActivityErrorCode.SESSION_ALREADY_COMPLETED);
        }
    }

    private void validateRegisterRequest(FocusSession focusSession, LiveActivityRegisterRequest request) {
        if (!focusSession.constellationId().equals(request.constellationId())) {
            throw new BusinessException(FocusSessionErrorCode.CONSTELLATION_MISMATCH);
        }

        int expectedSlotSeconds = focusSession.durationMinutes() * 60;
        if (expectedSlotSeconds != request.slotSeconds()) {
            throw new BusinessException(LiveActivityErrorCode.SLOT_SECONDS_MISMATCH);
        }
    }

    private FocusLiveActivityEntity updateRegistration(
            FocusLiveActivityEntity existing,
            LiveActivityRegisterRequest request
    ) {
        existing.updateRegistration(
                request.activityId(),
                request.pushToken(),
                request.startedAt(),
                request.slotSeconds(),
                request.constellationId(),
                request.rotationRadians()
        );

        if (existing.getStatus() == FocusLiveActivityStatus.ENDED) {
            if (request.status() == FocusLiveActivityStatus.PAUSED) {
                existing.markPaused(Instant.now(clock));
            } else {
                existing.markRunning();
            }
        }

        return existing;
    }

    private FocusLiveActivityEntity createRegistration(String userId, LiveActivityRegisterRequest request) {
        return new FocusLiveActivityEntity(
                request.focusSessionId(),
                userId,
                request.activityId(),
                request.pushToken(),
                request.status() == FocusLiveActivityStatus.PAUSED ? FocusLiveActivityStatus.PAUSED : FocusLiveActivityStatus.RUNNING,
                request.startedAt(),
                request.slotSeconds(),
                request.constellationId(),
                request.rotationRadians(),
                request.status() == FocusLiveActivityStatus.PAUSED ? Instant.now(clock) : null
        );
    }

    private FocusLiveActivityEntity loadRegistration(String userId, String focusSessionId) {
        FocusLiveActivityEntity registration = focusLiveActivityJpaRepository.findByFocusSessionId(focusSessionId)
                .orElseThrow(() -> new BusinessException(LiveActivityErrorCode.REGISTRATION_NOT_FOUND));

        if (!registration.getUserId().equals(userId)) {
            throw new BusinessException(LiveActivityErrorCode.REGISTRATION_NOT_FOUND);
        }

        return registration;
    }

    private Constellation loadConstellation(Long constellationId) {
        return loadConstellationPort.loadById(constellationId)
                .orElseThrow(() -> new BusinessException(FocusSessionErrorCode.CONSTELLATION_NOT_FOUND));
    }

    private void ensureRunning(FocusLiveActivityEntity registration) {
        if (registration.getStatus() != FocusLiveActivityStatus.RUNNING) {
            throw new BusinessException(LiveActivityErrorCode.INVALID_STATUS);
        }
    }

    private void ensurePaused(FocusLiveActivityEntity registration) {
        if (registration.getStatus() != FocusLiveActivityStatus.PAUSED) {
            throw new BusinessException(LiveActivityErrorCode.INVALID_STATUS);
        }
    }

    private void scheduleNextUpdate(
            FocusLiveActivityEntity registration,
            Constellation constellation,
            Instant now
    ) {
        ComputedState state = computeState(registration, constellation, now, false);
        if (state.discoveredStarCount() >= state.totalStarCount()) {
            log.info("No additional live activity updates to schedule: focusSessionId={}, discoveredStarCount={}",
                    registration.getFocusSessionId(), state.discoveredStarCount());
            liveActivityScheduler.cancel(registration.getFocusSessionId());
            return;
        }

        Instant nextUpdateAt = computeNextDiscoveryTime(
                registration,
                state.totalStarCount(),
                state.discoveredStarCount() + 1
        );
        log.info(
                "Registering next live activity schedule: focusSessionId={}, nextDiscoveredStarCount={}, scheduledAt={}",
                registration.getFocusSessionId(),
                state.discoveredStarCount() + 1,
                nextUpdateAt
        );

        liveActivityScheduler.schedule(
                registration.getFocusSessionId(),
                nextUpdateAt,
                () -> transactionTemplate.executeWithoutResult(status -> processScheduledUpdate(registration.getFocusSessionId()))
        );
    }

    private Instant computeNextDiscoveryTime(
            FocusLiveActivityEntity registration,
            int totalStarCount,
            int nextStarCount
    ) {
        double intervalMillis = registration.getSlotSeconds() * 1000.0 / totalStarCount;
        long targetActiveMillis = (long) Math.ceil(nextStarCount * intervalMillis);
        return registration.getStartedAt()
                .plusSeconds(registration.getPausedAccumulatedSeconds())
                .plusMillis(targetActiveMillis);
    }

    private ComputedState computeState(
            FocusLiveActivityEntity registration,
            Constellation constellation,
            Instant referenceInstant,
            boolean forceCompleted
    ) {
        int totalStarCount = Math.max(1, constellation.stars().size());
        Instant effectiveInstant = registration.getStatus() == FocusLiveActivityStatus.PAUSED && registration.getPausedAt() != null
                ? registration.getPausedAt()
                : referenceInstant;

        long activeElapsedSeconds = forceCompleted
                ? registration.getSlotSeconds()
                : computeActiveElapsedSeconds(registration, effectiveInstant);
        int discoveredStarCount = forceCompleted
                ? totalStarCount
                : computeDiscoveredStarCount(activeElapsedSeconds, registration.getSlotSeconds(), totalStarCount);
        int remainingSeconds = forceCompleted
                ? 0
                : Math.max(registration.getSlotSeconds() - Math.toIntExact(activeElapsedSeconds), 0);

        String payloadStatus = forceCompleted
                ? "running"
                : registration.getStatus().name().toLowerCase(Locale.ROOT);

        return new ComputedState(
                payloadStatus,
                remainingSeconds,
                registration.getSlotSeconds(),
                registration.getStartedAt().plusSeconds(registration.getPausedAccumulatedSeconds()).getEpochSecond(),
                (double) activeElapsedSeconds,
                discoveredStarCount,
                totalStarCount,
                buildPreview(constellation, registration.getRotationRadians())
        );
    }

    private long computeActiveElapsedSeconds(FocusLiveActivityEntity registration, Instant effectiveInstant) {
        long elapsed = Duration.between(registration.getStartedAt(), effectiveInstant).getSeconds() - registration.getPausedAccumulatedSeconds();
        return Math.max(elapsed, 0L);
    }

    private int computeDiscoveredStarCount(long activeElapsedSeconds, int slotSeconds, int totalStarCount) {
        if (activeElapsedSeconds <= 0) {
            return 0;
        }

        double interval = (double) slotSeconds / totalStarCount;
        int discovered = (int) Math.floor(activeElapsedSeconds / interval);
        return Math.max(0, Math.min(discovered, totalStarCount));
    }

    private ApnsClient.ConstellationPreview buildPreview(Constellation constellation, double rotationRadians) {
        List<ConstellationStar> stars = constellation.stars().stream()
                .sorted(Comparator.comparing(ConstellationStar::id, Comparator.nullsLast(Long::compareTo)))
                .toList();

        Map<Long, Integer> indexes = java.util.stream.IntStream.range(0, stars.size())
                .boxed()
                .collect(Collectors.toMap(index -> stars.get(index).id(), index -> index));

        List<ApnsClient.PreviewEdge> edges = constellation.edges().stream()
                .sorted(Comparator.comparing(ConstellationEdge::id, Comparator.nullsLast(Long::compareTo)))
                .map(edge -> {
                    Integer fromIndex = indexes.get(edge.fromStarId());
                    Integer toIndex = indexes.get(edge.toStarId());
                    if (fromIndex == null || toIndex == null) {
                        return null;
                    }
                    return new ApnsClient.PreviewEdge(fromIndex, toIndex);
                })
                .filter(Objects::nonNull)
                .toList();

        return new ApnsClient.ConstellationPreview(
                stars.stream()
                        .map(star -> new ApnsClient.PreviewStar(star.vectorX(), star.vectorY()))
                        .toList(),
                edges,
                rotationRadians
        );
    }

    private void sendPushAndUpdateState(
            FocusLiveActivityEntity registration,
            String event,
            ComputedState state,
            Instant pushedAt
    ) {
        log.info(
                "Sending APNs live activity {}: focusSessionId={}, activityId={}, pushToken={}, discoveredStarCount={}, remainingSeconds={}, status={}, referenceDate={}",
                event,
                registration.getFocusSessionId(),
                registration.getActivityId(),
                maskPushToken(registration.getPushToken()),
                state.discoveredStarCount(),
                state.remainingSeconds(),
                state.status(),
                state.referenceDate()
        );

        ApnsClient.ApnsPushResult result = apnsClient.send(
                registration.getPushToken(),
                event,
                new ApnsClient.ContentState(
                        state.status(),
                        state.remainingSeconds(),
                        state.totalSeconds(),
                        state.referenceDate(),
                        state.activeElapsedSeconds(),
                        state.discoveredStarCount(),
                        state.totalStarCount(),
                        state.constellationPreview()
                ),
                pushedAt
        );

        log.info(
                "APNs response: focusSessionId={}, statusCode={}, apnsId={}, reason={}",
                registration.getFocusSessionId(),
                result.statusCode(),
                result.apnsId(),
                result.reason()
        );

        if (result.accepted()) {
            registration.markLastPushed(state.discoveredStarCount(), pushedAt);
            log.info(
                    "Updated live activity last pushed state: focusSessionId={}, discoveredStarCount={}",
                    registration.getFocusSessionId(),
                    state.discoveredStarCount()
            );
            return;
        }

        if (result.terminalTokenFailure()) {
            registration.markEnded();
            liveActivityScheduler.cancel(registration.getFocusSessionId());
            log.warn(
                    "Disabled live activity token after APNs terminal failure: focusSessionId={}, reason={}",
                    registration.getFocusSessionId(),
                    result.reason()
            );
            return;
        }

        log.warn(
                "APNs live activity push failed after retry: focusSessionId={}, statusCode={}, reason={}",
                registration.getFocusSessionId(),
                result.statusCode(),
                result.reason()
        );
    }

    private String maskPushToken(String pushToken) {
        if (pushToken == null || pushToken.isBlank()) {
            return "<empty>";
        }
        if (pushToken.length() <= 12) {
            return pushToken;
        }
        return pushToken.substring(0, 6) + "..." + pushToken.substring(pushToken.length() - 6);
    }

    private record ComputedState(
            String status,
            int remainingSeconds,
            int totalSeconds,
            long referenceDate,
            double activeElapsedSeconds,
            int discoveredStarCount,
            int totalStarCount,
            ApnsClient.ConstellationPreview constellationPreview
    ) {
    }
}
