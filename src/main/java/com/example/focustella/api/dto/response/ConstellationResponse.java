package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.Constellation;
import java.time.LocalDateTime;
import java.util.List;

public record ConstellationResponse(
        Long id,
        String name,
        Long createdBy,
        Integer starCount,
        Double defaultScale,
        Double minScale,
        Double maxScale,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<StarResponse> stars,
        List<EdgeResponse> edges
) {

    public static ConstellationResponse from(Constellation constellation) {
        return new ConstellationResponse(
                constellation.id(),
                constellation.name(),
                constellation.createdBy(),
                constellation.starCount(),
                constellation.defaultScale(),
                constellation.minScale(),
                constellation.maxScale(),
                constellation.createdAt(),
                constellation.updatedAt(),
                constellation.stars().stream()
                        .map(star -> new StarResponse(star.id(), star.vectorX(), star.vectorY()))
                        .toList(),
                constellation.edges().stream()
                        .map(edge -> new EdgeResponse(edge.id(), edge.fromStarId(), edge.toStarId()))
                        .toList()
        );
    }

    public record StarResponse(
            Long id,
            Double vectorX,
            Double vectorY
    ) {
    }

    public record EdgeResponse(
            Long id,
            Long fromStarId,
            Long toStarId
    ) {
    }
}
