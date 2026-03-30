package com.example.focustella.infrastructure.persistence.entity;

import com.example.focustella.domain.model.FocusLiveActivityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "focus_live_activity",
        uniqueConstraints = @UniqueConstraint(name = "uk_focus_live_activity_session", columnNames = "focus_session_id")
)
public class FocusLiveActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "focus_session_id", nullable = false, length = 36)
    private String focusSessionId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "activity_id", nullable = false, length = 100)
    private String activityId;

    @Column(name = "push_token", nullable = false, length = 512)
    private String pushToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FocusLiveActivityStatus status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "slot_seconds", nullable = false)
    private Integer slotSeconds;

    @Column(name = "constellation_id", nullable = false)
    private Long constellationId;

    @Column(name = "rotation_radians", nullable = false)
    private Double rotationRadians;

    @Column(name = "paused_accumulated_seconds", nullable = false)
    private Long pausedAccumulatedSeconds;

    @Column(name = "paused_at")
    private Instant pausedAt;

    @Column(name = "last_pushed_discovered_count")
    private Integer lastPushedDiscoveredCount;

    @Column(name = "last_pushed_at")
    private Instant lastPushedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public FocusLiveActivityEntity(
            String focusSessionId,
            String userId,
            String activityId,
            String pushToken,
            FocusLiveActivityStatus status,
            Instant startedAt,
            Integer slotSeconds,
            Long constellationId,
            Double rotationRadians,
            Instant pausedAt
    ) {
        this.focusSessionId = focusSessionId;
        this.userId = userId;
        this.activityId = activityId;
        this.pushToken = pushToken;
        this.status = status;
        this.startedAt = startedAt;
        this.slotSeconds = slotSeconds;
        this.constellationId = constellationId;
        this.rotationRadians = rotationRadians;
        this.pausedAccumulatedSeconds = 0L;
        this.pausedAt = pausedAt;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (pausedAccumulatedSeconds == null) {
            pausedAccumulatedSeconds = 0L;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateRegistration(
            String activityId,
            String pushToken,
            Instant startedAt,
            Integer slotSeconds,
            Long constellationId,
            Double rotationRadians
    ) {
        this.activityId = activityId;
        this.pushToken = pushToken;
        this.startedAt = startedAt;
        this.slotSeconds = slotSeconds;
        this.constellationId = constellationId;
        this.rotationRadians = rotationRadians;
    }

    public void markRunning() {
        this.status = FocusLiveActivityStatus.RUNNING;
        this.pausedAt = null;
    }

    public void markPaused(Instant pausedAt) {
        this.status = FocusLiveActivityStatus.PAUSED;
        this.pausedAt = pausedAt;
    }

    public void resume(Instant resumedAt) {
        if (pausedAt != null && !resumedAt.isBefore(pausedAt)) {
            pausedAccumulatedSeconds += Duration.between(pausedAt, resumedAt).getSeconds();
        }
        this.status = FocusLiveActivityStatus.RUNNING;
        this.pausedAt = null;
    }

    public void markEnded() {
        this.status = FocusLiveActivityStatus.ENDED;
        this.pausedAt = null;
    }

    public void markLastPushed(int discoveredStarCount, Instant pushedAt) {
        this.lastPushedDiscoveredCount = discoveredStarCount;
        this.lastPushedAt = pushedAt;
    }
}
