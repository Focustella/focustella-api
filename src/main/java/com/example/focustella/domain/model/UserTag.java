package com.example.focustella.domain.model;

import java.time.LocalDateTime;

public record UserTag(
        Long id,
        String userId,
        String name,
        String normalizedName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
