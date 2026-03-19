package com.example.focustella.domain.model;

public record ConstellationEdge(
        Long id,
        Long fromStarId,
        Long toStarId
) {
}
