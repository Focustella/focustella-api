package com.example.focustella.domain.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record FocusSession(
        String id,
        String userId,
        Long constellationId,
        Integer durationMinutes,
        Instant startedAt,
        Instant endedAt,
        Integer slotSeconds,
        Integer discoveredStarCount,
        List<String> topicTags,
        Integer rating,
        String freeText,
        FocusSessionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
