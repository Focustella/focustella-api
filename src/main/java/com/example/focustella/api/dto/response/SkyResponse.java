package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.DailySession;
import com.example.focustella.domain.model.Sky;
import java.time.LocalDateTime;
import java.util.List;

public record SkyResponse(
        String ownerId,
        Long seed,
        List<DailyStarResponse> dailyStars,
        List<FocusConstellationResponse> focusConstellations
) {
    public static SkyResponse from(Sky sky) {
        return new SkyResponse(
                sky.ownerId(),
                sky.seed(),
                sky.dailyStars().stream()
                        .map(star -> new DailyStarResponse(
                                star.sessionId(),
                                DailySessionDetailResponse.from(star.session())
                        ))
                        .toList(),
                sky.focusConstellations().stream()
                        .map(item -> new FocusConstellationResponse(
                                item.focusSession().id(),
                                item.focusSession().constellationId(),
                                item.focusSession().durationMinutes(),
                                item.focusSession().startedAt(),
                                item.focusSession().endedAt(),
                                item.focusSession().slotSeconds(),
                                item.focusSession().discoveredStarCount(),
                                item.focusSession().topicTags(),
                                item.focusSession().rating(),
                                item.focusSession().freeText(),
                                ConstellationResponse.from(item.constellation())
                        ))
                        .toList()
        );
    }

    public record DailyStarResponse(
            String sessionId,
            DailySessionDetailResponse session
    ) {
    }

    public record DailySessionDetailResponse(
            String sessionId,
            String userId,
            LocalDateTime timestamp,
            List<ChecklistItemResponse> checklists
    ) {
        public static DailySessionDetailResponse from(DailySession session) {
            return new DailySessionDetailResponse(
                    session.getSessionUuid(),
                    session.getUserUuid(),
                    session.getTimestamp(),
                    session.getChecklists().stream()
                            .map(item -> new ChecklistItemResponse(
                                    item.getItemUuid(),
                                    item.getTitle(),
                                    item.getIsCompleted()
                            ))
                            .toList()
            );
        }
    }

    public record FocusConstellationResponse(
            String sessionId,
            Long constellationId,
            Integer durationMinutes,
            java.time.Instant startedAt,
            java.time.Instant endedAt,
            Integer slotSeconds,
            Integer discoveredStarCount,
            List<String> topicTags,
            Integer rating,
            String freeText,
            ConstellationResponse constellation
    ) {
    }
}
