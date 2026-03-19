package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.FocusSession;
import com.example.focustella.domain.model.FocusSessionStatus;
import java.time.Instant;
import java.util.List;

public record FocusSessionResponse(
        String sessionId,
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
        ConstellationResponse constellation
) {
    public static FocusSessionResponse from(FocusSession session, ConstellationResponse constellation) {
        return new FocusSessionResponse(
                session.id(),
                session.constellationId(),
                session.durationMinutes(),
                session.startedAt(),
                session.endedAt(),
                session.slotSeconds(),
                session.discoveredStarCount(),
                session.topicTags(),
                session.rating(),
                session.freeText(),
                session.status(),
                constellation
        );
    }
}
