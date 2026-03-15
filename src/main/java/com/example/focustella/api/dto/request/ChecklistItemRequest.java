package com.example.focustella.api.dto.request;

public record ChecklistItemRequest(
        String title,
        Boolean isCompleted
) {
}