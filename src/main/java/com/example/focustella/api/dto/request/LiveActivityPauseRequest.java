package com.example.focustella.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record LiveActivityPauseRequest(
        @NotBlank String focusSessionId,
        @NotNull Instant pausedAt
) {
}
