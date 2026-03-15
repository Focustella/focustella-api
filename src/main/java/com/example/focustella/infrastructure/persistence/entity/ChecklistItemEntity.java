package com.example.focustella.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "checklist_item")
public class ChecklistItemEntity {

    @Id
    @Column(name = "item_uuid", length = 36)
    private String itemUuid;

    @Column(nullable = false)
    private String title;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_session_uuid")
    private DailySessionEntity dailySession;

    @PrePersist
    public void prePersist() {
        if (itemUuid == null) {
            itemUuid = UUID.randomUUID().toString();
        }
    }

    public ChecklistItemEntity(String title, Boolean isCompleted) {
        this.title = title;
        this.isCompleted = isCompleted;
    }
}