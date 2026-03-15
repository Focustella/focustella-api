package com.example.focustella.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class DailySession {
    private final String sessionUuid;
    private final String userUuid;
    private final LocalDateTime timestamp;
    private final List<ChecklistItem> checklists;

    public DailySession(String sessionUuid, String userUuid, LocalDateTime timestamp, List<ChecklistItem> checklists) {
        this.sessionUuid = sessionUuid;
        this.userUuid = userUuid;
        this.timestamp = timestamp;
        this.checklists = checklists;
    }

}