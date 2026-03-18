package com.example.focustella.domain.model;

import java.util.List;

public record Sky(
        String ownerId,
        Long seed,
        List<SkyDailyStar> dailyStars
) {
}
