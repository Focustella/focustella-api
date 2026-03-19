package com.example.focustella.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public record FocusSessionSaveRequest(
        @NotBlank String sessionId,
        @NotNull Long constellationId,
        @NotNull Instant startedAt,
        @NotNull Instant endedAt,
        @NotNull @Min(1) Integer slotSeconds,
        @NotNull @Min(0) Integer discoveredStarCount,
        List<String> topicTags,
        @Min(1) @Max(5) Integer rating,
        String freeText
) {
}
