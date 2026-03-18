package com.example.focustella.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record DailySessionSaveRequest(
        @NotNull LocalDateTime timestamp,
        @Valid List<ChecklistItemRequest> checklists
) {
}
