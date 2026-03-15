package com.example.focustella.api.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record DailySessionSaveRequest(
        LocalDateTime timestamp,
        List<ChecklistItemRequest> checklists
) {
}