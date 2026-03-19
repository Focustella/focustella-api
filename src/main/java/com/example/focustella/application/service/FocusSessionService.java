package com.example.focustella.application.service;

import com.example.focustella.api.dto.request.FocusSessionSaveRequest;
import com.example.focustella.api.dto.response.ConstellationResponse;
import com.example.focustella.api.dto.response.FocusSessionCreateResponse;
import com.example.focustella.api.dto.response.FocusSessionResponse;
import com.example.focustella.application.port.in.CreateFocusSessionUseCase;
import com.example.focustella.application.port.in.GetFocusSessionUseCase;
import com.example.focustella.application.port.in.SaveFocusSessionUseCase;
import com.example.focustella.application.port.out.LoadConstellationPort;
import com.example.focustella.application.port.out.LoadFocusSessionPort;
import com.example.focustella.application.port.out.LoadUserPort;
import com.example.focustella.application.port.out.SaveFocusSessionPort;
import com.example.focustella.common.exception.BusinessException;
import com.example.focustella.common.exception.code.CommonErrorCode;
import com.example.focustella.common.exception.code.FocusSessionErrorCode;
import com.example.focustella.domain.model.Constellation;
import com.example.focustella.domain.model.FocusSession;
import com.example.focustella.domain.model.FocusSessionStatus;
import com.example.focustella.domain.model.User;
import com.example.focustella.infrastructure.config.FocusConstellationProperties;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FocusSessionService implements CreateFocusSessionUseCase, SaveFocusSessionUseCase, GetFocusSessionUseCase {

    private final LoadUserPort loadUserPort;
    private final LoadConstellationPort loadConstellationPort;
    private final LoadFocusSessionPort loadFocusSessionPort;
    private final SaveFocusSessionPort saveFocusSessionPort;
    private final FocusConstellationProperties focusConstellationProperties;

    public FocusSessionService(
            LoadUserPort loadUserPort,
            LoadConstellationPort loadConstellationPort,
            LoadFocusSessionPort loadFocusSessionPort,
            SaveFocusSessionPort saveFocusSessionPort,
            FocusConstellationProperties focusConstellationProperties
    ) {
        this.loadUserPort = loadUserPort;
        this.loadConstellationPort = loadConstellationPort;
        this.loadFocusSessionPort = loadFocusSessionPort;
        this.saveFocusSessionPort = saveFocusSessionPort;
        this.focusConstellationProperties = focusConstellationProperties;
    }

    @Override
    @Transactional
    public FocusSessionCreateResponse prepare(String userId, Integer durationMinutes) {
        User user = loadUserPort.loadById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        FocusConstellationProperties.Rule rule = resolveRule(durationMinutes);
        Set<Long> usedConstellationIds = loadFocusSessionPort.loadUsedConstellationIdsByUserId(userId);

        List<Constellation> candidates = loadConstellationPort.loadActiveByStarCountRange(
                        rule.getMinStarCount(),
                        rule.getMaxStarCount()
                ).stream()
                .filter(constellation -> !usedConstellationIds.contains(constellation.id()))
                .sorted(Comparator.comparing(Constellation::id))
                .toList();

        if (candidates.isEmpty()) {
            candidates = loadConstellationPort.loadActiveByStarCountRange(1, Integer.MAX_VALUE).stream()
                    .filter(constellation -> !usedConstellationIds.contains(constellation.id()))
                    .sorted(Comparator.comparing(Constellation::id))
                    .toList();
        }

        if (candidates.isEmpty()) {
            throw new BusinessException(FocusSessionErrorCode.CONSTELLATION_EXHAUSTED);
        }

        Random random = new Random(user.seed() ^ durationMinutes.longValue());
        Constellation selected = candidates.get(random.nextInt(candidates.size()));
        FocusSession plannedSession = saveFocusSessionPort.save(new FocusSession(
                UUID.randomUUID().toString(),
                userId,
                selected.id(),
                durationMinutes,
                null,
                null,
                null,
                null,
                List.of(),
                null,
                null,
                FocusSessionStatus.PLANNED,
                null,
                null
        ));

        return new FocusSessionCreateResponse(
                plannedSession.id(),
                selected.id(),
                durationMinutes,
                rule.getMinStarCount(),
                rule.getMaxStarCount(),
                ConstellationResponse.from(selected)
        );
    }

    @Override
    @Transactional
    public FocusSessionResponse save(String userId, FocusSessionSaveRequest request) {
        FocusSession planned = loadFocusSessionPort.loadByIdAndUserId(request.sessionId(), userId)
                .orElseThrow(() -> new BusinessException(FocusSessionErrorCode.SESSION_NOT_FOUND));

        if (planned.status() != FocusSessionStatus.PLANNED) {
            throw new BusinessException(FocusSessionErrorCode.INVALID_STATUS);
        }

        if (!planned.constellationId().equals(request.constellationId())) {
            throw new BusinessException(FocusSessionErrorCode.CONSTELLATION_MISMATCH);
        }

        Constellation constellation = loadConstellationPort.loadById(request.constellationId())
                .orElseThrow(() -> new BusinessException(FocusSessionErrorCode.CONSTELLATION_NOT_FOUND));

        FocusSession saved = saveFocusSessionPort.save(new FocusSession(
                planned.id(),
                planned.userId(),
                planned.constellationId(),
                planned.durationMinutes(),
                request.startedAt(),
                request.endedAt(),
                request.slotSeconds(),
                request.discoveredStarCount(),
                request.topicTags() == null ? List.of() : request.topicTags(),
                request.rating(),
                request.freeText(),
                FocusSessionStatus.COMPLETED,
                planned.createdAt(),
                planned.updatedAt()
        ));

        return FocusSessionResponse.from(saved, ConstellationResponse.from(constellation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FocusSessionResponse> getCompletedSessions(String userId) {
        return loadFocusSessionPort.loadByUserId(userId).stream()
                .filter(session -> session.status() == FocusSessionStatus.COMPLETED)
                .map(session -> {
                    Constellation constellation = loadConstellationPort.loadById(session.constellationId())
                            .orElseThrow(() -> new BusinessException(FocusSessionErrorCode.CONSTELLATION_NOT_FOUND));
                    return FocusSessionResponse.from(session, ConstellationResponse.from(constellation));
                })
                .toList();
    }

    private FocusConstellationProperties.Rule resolveRule(Integer durationMinutes) {
        return focusConstellationProperties.getRules().stream()
                .filter(rule -> durationMinutes >= rule.getMinMinutes() && durationMinutes <= rule.getMaxMinutes())
                .findFirst()
                .orElseThrow(() -> new BusinessException(FocusSessionErrorCode.RULE_NOT_FOUND));
    }
}
