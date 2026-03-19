package com.example.focustella.domain.model;

import java.util.List;

public record ConstellationDraft(
        String name,
        Long createdBy,
        Integer starCount,
        Double defaultScale,
        Double minScale,
        Double maxScale,
        List<ConstellationStarDraft> stars,
        List<ConstellationEdgeDraft> edges
) {
}
