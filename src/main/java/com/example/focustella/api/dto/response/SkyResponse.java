package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.DailySession;
import com.example.focustella.domain.model.Sky;
import java.time.LocalDateTime;
import java.util.List;

public record SkyResponse(
        String ownerId,
        Long seed,
        List<DailyStarResponse> dailyStars,
        List<Object> focusConstellations
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
                List.of()
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
}
