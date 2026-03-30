package com.example.focustella.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.focustella.api.dto.request.LiveActivityEndRequest;
import com.example.focustella.api.dto.request.LiveActivityPauseRequest;
import com.example.focustella.api.dto.request.LiveActivityRegisterRequest;
import com.example.focustella.api.dto.request.LiveActivityResumeRequest;
import com.example.focustella.application.port.out.LoadConstellationPort;
import com.example.focustella.application.port.out.LoadFocusSessionPort;
import com.example.focustella.domain.model.Constellation;
import com.example.focustella.domain.model.ConstellationEdge;
import com.example.focustella.domain.model.ConstellationStar;
import com.example.focustella.domain.model.FocusLiveActivityEndReason;
import com.example.focustella.domain.model.FocusLiveActivityStatus;
import com.example.focustella.domain.model.FocusSession;
import com.example.focustella.domain.model.FocusSessionStatus;
import com.example.focustella.infrastructure.external.ApnsClient;
import com.example.focustella.infrastructure.persistence.entity.FocusLiveActivityEntity;
import com.example.focustella.infrastructure.persistence.repository.FocusLiveActivityJpaRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(MockitoExtension.class)
class LiveActivityServiceTest {

    private static final Instant NOW = Instant.parse("2026-03-23T06:20:00Z");
    private static final String USER_ID = "user-1";
    private static final String SESSION_ID = "b1fdb085-1e47-4747-a7df-4814ba4e25cd";

    @Mock
    private LoadFocusSessionPort loadFocusSessionPort;

    @Mock
    private LoadConstellationPort loadConstellationPort;

    @Mock
    private FocusLiveActivityJpaRepository focusLiveActivityJpaRepository;

    @Mock
    private LiveActivityScheduler liveActivityScheduler;

    @Mock
    private ApnsClient apnsClient;

    @Mock
    private PlatformTransactionManager transactionManager;

    private LiveActivityService liveActivityService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        liveActivityService = new LiveActivityService(
                loadFocusSessionPort,
                loadConstellationPort,
                focusLiveActivityJpaRepository,
                liveActivityScheduler,
                apnsClient,
                clock,
                transactionManager
        );

        given(focusLiveActivityJpaRepository.save(any(FocusLiveActivityEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        Mockito.lenient().when(apnsClient.send(any(), any(), any(), any()))
                .thenReturn(new ApnsClient.ApnsPushResult(true, 200, "apns-id", null, false, false));
    }

    @Test
    void register_schedulesNextDiscoveryAtExpectedTime() {
        FocusSession focusSession = focusSession(FocusSessionStatus.PLANNED);
        given(loadFocusSessionPort.loadByIdAndUserId(SESSION_ID, USER_ID)).willReturn(Optional.of(focusSession));
        given(focusLiveActivityJpaRepository.findByFocusSessionId(SESSION_ID)).willReturn(Optional.empty());
        given(loadConstellationPort.loadById(1L)).willReturn(Optional.of(constellation()));

        liveActivityService.register(USER_ID, new LiveActivityRegisterRequest(
                SESSION_ID,
                "activity-1",
                "push-token",
                NOW,
                1500,
                FocusLiveActivityStatus.RUNNING,
                1L,
                1.0471975512
        ));

        ArgumentCaptor<Instant> whenCaptor = ArgumentCaptor.forClass(Instant.class);
        then(liveActivityScheduler).should().schedule(eq(SESSION_ID), whenCaptor.capture(), any(Runnable.class));
        assertThat(whenCaptor.getValue()).isEqualTo(Instant.parse("2026-03-23T06:25:00Z"));
    }

    @Test
    void pause_sendsPausedUpdateAndCancelsSchedule() {
        FocusLiveActivityEntity registration = new FocusLiveActivityEntity(
                SESSION_ID,
                USER_ID,
                "activity-1",
                "push-token",
                FocusLiveActivityStatus.RUNNING,
                NOW,
                1500,
                1L,
                1.0471975512,
                null
        );
        Instant pausedAt = Instant.parse("2026-03-23T06:28:14Z");

        given(focusLiveActivityJpaRepository.findByFocusSessionId(SESSION_ID)).willReturn(Optional.of(registration));
        given(loadFocusSessionPort.loadByIdAndUserId(SESSION_ID, USER_ID)).willReturn(Optional.of(focusSession(FocusSessionStatus.PLANNED)));
        given(loadConstellationPort.loadById(1L)).willReturn(Optional.of(constellation()));

        liveActivityService.pause(USER_ID, new LiveActivityPauseRequest(SESSION_ID, pausedAt));

        ArgumentCaptor<ApnsClient.ContentState> stateCaptor = ArgumentCaptor.forClass(ApnsClient.ContentState.class);
        then(apnsClient).should().send(eq("push-token"), eq("update"), stateCaptor.capture(), eq(pausedAt));
        then(liveActivityScheduler).should().cancel(SESSION_ID);

        ApnsClient.ContentState payload = stateCaptor.getValue();
        assertThat(payload.status()).isEqualTo("paused");
        assertThat(payload.discoveredStarCount()).isEqualTo(1);
        assertThat(payload.remainingSeconds()).isEqualTo(1006);
    }

    @Test
    void resume_sendsRunningUpdateAndSchedulesNextDiscoveryWithPauseOffset() {
        FocusLiveActivityEntity registration = new FocusLiveActivityEntity(
                SESSION_ID,
                USER_ID,
                "activity-1",
                "push-token",
                FocusLiveActivityStatus.PAUSED,
                NOW,
                1500,
                1L,
                1.0471975512,
                Instant.parse("2026-03-23T06:28:14Z")
        );
        Instant resumedAt = Instant.parse("2026-03-23T06:31:02Z");

        given(focusLiveActivityJpaRepository.findByFocusSessionId(SESSION_ID)).willReturn(Optional.of(registration));
        given(loadFocusSessionPort.loadByIdAndUserId(SESSION_ID, USER_ID)).willReturn(Optional.of(focusSession(FocusSessionStatus.PLANNED)));
        given(loadConstellationPort.loadById(1L)).willReturn(Optional.of(constellation()));

        liveActivityService.resume(USER_ID, new LiveActivityResumeRequest(SESSION_ID, resumedAt));

        ArgumentCaptor<ApnsClient.ContentState> stateCaptor = ArgumentCaptor.forClass(ApnsClient.ContentState.class);
        ArgumentCaptor<Instant> whenCaptor = ArgumentCaptor.forClass(Instant.class);

        then(apnsClient).should().send(eq("push-token"), eq("update"), stateCaptor.capture(), eq(resumedAt));
        then(liveActivityScheduler).should().schedule(eq(SESSION_ID), whenCaptor.capture(), any(Runnable.class));

        ApnsClient.ContentState payload = stateCaptor.getValue();
        assertThat(payload.status()).isEqualTo("running");
        assertThat(payload.discoveredStarCount()).isEqualTo(1);
        assertThat(whenCaptor.getValue()).isEqualTo(Instant.parse("2026-03-23T06:32:48Z"));
    }

    @Test
    void end_completedSessionSendsFinalPayload() {
        FocusLiveActivityEntity registration = new FocusLiveActivityEntity(
                SESSION_ID,
                USER_ID,
                "activity-1",
                "push-token",
                FocusLiveActivityStatus.RUNNING,
                NOW,
                1500,
                1L,
                1.0471975512,
                null
        );
        Instant endedAt = Instant.parse("2026-03-23T06:45:00Z");

        given(focusLiveActivityJpaRepository.findByFocusSessionId(SESSION_ID)).willReturn(Optional.of(registration));
        given(loadConstellationPort.loadById(1L)).willReturn(Optional.of(constellation()));

        liveActivityService.end(USER_ID, new LiveActivityEndRequest(SESSION_ID, endedAt, FocusLiveActivityEndReason.COMPLETED));

        ArgumentCaptor<ApnsClient.ContentState> stateCaptor = ArgumentCaptor.forClass(ApnsClient.ContentState.class);
        then(apnsClient).should().send(eq("push-token"), eq("end"), stateCaptor.capture(), eq(endedAt));

        ApnsClient.ContentState payload = stateCaptor.getValue();
        assertThat(payload.discoveredStarCount()).isEqualTo(5);
        assertThat(payload.remainingSeconds()).isZero();
        assertThat(payload.status()).isEqualTo("running");
    }

    private FocusSession focusSession(FocusSessionStatus status) {
        return new FocusSession(
                SESSION_ID,
                USER_ID,
                1L,
                25,
                null,
                null,
                null,
                null,
                List.of(),
                null,
                null,
                status,
                LocalDateTime.of(2026, 3, 23, 15, 0),
                LocalDateTime.of(2026, 3, 23, 15, 0)
        );
    }

    private Constellation constellation() {
        return new Constellation(
                1L,
                "Cassiopeia",
                null,
                5,
                1.0,
                0.8,
                1.4,
                LocalDateTime.of(2026, 3, 23, 15, 0),
                LocalDateTime.of(2026, 3, 23, 15, 0),
                List.of(
                        new ConstellationStar(10L, -18.5, 12.0),
                        new ConstellationStar(11L, -4.0, 3.5),
                        new ConstellationStar(12L, 8.0, -2.0),
                        new ConstellationStar(13L, 14.0, 6.0),
                        new ConstellationStar(14L, 20.0, -7.0)
                ),
                List.of(
                        new ConstellationEdge(100L, 10L, 11L),
                        new ConstellationEdge(101L, 11L, 12L),
                        new ConstellationEdge(102L, 12L, 13L),
                        new ConstellationEdge(103L, 13L, 14L)
                )
        );
    }
}
