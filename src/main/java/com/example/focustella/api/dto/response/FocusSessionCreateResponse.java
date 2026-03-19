package com.example.focustella.api.dto.response;

public record FocusSessionCreateResponse(
        String focusSessionId,
        Long constellationId,
        Integer durationMinutes,
        Integer minStarCount,
        Integer maxStarCount,
        ConstellationResponse constellation
) {
}
