package com.example.focustella.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "daily_session")
public class DailySessionEntity {

    @Id
    @Column(name = "session_uuid", updatable = false, nullable = false, length = 36)
    private String sessionUuid;

    @Column(name = "user_uuid", nullable = false, length = 36)
    private String userUuid;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "dailySession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChecklistItemEntity> checklists = new ArrayList<>();

    public DailySessionEntity(String sessionUuid, String userUuid, LocalDateTime timestamp) {
        this.sessionUuid = sessionUuid;
        this.userUuid = userUuid;
        this.timestamp = timestamp;
    }

    public void addChecklistItem(ChecklistItemEntity checklistItem) {
        this.checklists.add(checklistItem);
        checklistItem.setDailySession(this);
    }

    public void setChecklists(List<ChecklistItemEntity> checklists) {
        this.checklists.clear();
        if (checklists != null) {
            checklists.forEach(this::addChecklistItem);
        }
    }
}