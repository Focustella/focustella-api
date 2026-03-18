package com.example.focustella.domain.model;

import java.time.LocalDateTime;

public record User(
        String id,
        Long seed,
        UserType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
