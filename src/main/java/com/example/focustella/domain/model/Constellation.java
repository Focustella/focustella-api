package com.example.focustella.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public record Constellation(
        Long id,
        String name,
        Long createdBy,
        Integer starCount,
        Double defaultScale,
        Double minScale,
        Double maxScale,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ConstellationStar> stars,
        List<ConstellationEdge> edges
) {
}
