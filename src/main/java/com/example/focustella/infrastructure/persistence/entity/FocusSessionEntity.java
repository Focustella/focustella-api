package com.example.focustella.infrastructure.persistence.entity;

import com.example.focustella.domain.model.FocusSessionStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "focus_session")
public class FocusSessionEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "constellation_id", nullable = false)
    private ConstellationEntity constellation;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "slot_seconds")
    private Integer slotSeconds;

    @Column(name = "discovered_star_count")
    private Integer discoveredStarCount;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "focus_session_topic_tag", joinColumns = @JoinColumn(name = "focus_session_id"))
    @Column(name = "tag", nullable = false)
    private List<String> topicTags = new ArrayList<>();

    @Column
    private Integer rating;

    @Column(name = "free_text", length = 1000)
    private String freeText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FocusSessionStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public FocusSessionEntity(
            String id,
            String userId,
            ConstellationEntity constellation,
            Integer durationMinutes,
            FocusSessionStatus status
    ) {
        this.id = id;
        this.userId = userId;
        this.constellation = constellation;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void complete(
            Instant startedAt,
            Instant endedAt,
            Integer slotSeconds,
            Integer discoveredStarCount,
            List<String> topicTags,
            Integer rating,
            String freeText
    ) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.slotSeconds = slotSeconds;
        this.discoveredStarCount = discoveredStarCount;
        this.topicTags.clear();
        if (topicTags != null) {
            this.topicTags.addAll(topicTags);
        }
        this.rating = rating;
        this.freeText = freeText;
        this.status = FocusSessionStatus.COMPLETED;
    }
}
