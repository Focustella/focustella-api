package com.example.focustella.api.dto.response;

public record ChecklistItemResponse(
    String itemUuid,
    String title,
    Boolean isCompleted
) {}