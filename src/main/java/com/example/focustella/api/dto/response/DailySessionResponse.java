package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.DailySession;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record DailySessionResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime timestamp,
        List<ChecklistItemResponse> checklists
) {
    public static DailySessionResponse from(DailySession session) {
        return new DailySessionResponse(
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