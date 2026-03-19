package com.example.focustella.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ConstellationCreateRequest(
        @Schema(description = "Constellation display name", example = "Orion Starter")
        @NotBlank String name,
        @Schema(description = "Creator user id. Nullable for system-generated constellations", example = "1", nullable = true)
        Long createdBy,
        @Schema(description = "Default scale applied by the client", example = "1.0")
        @DecimalMin(value = "0.1") Double defaultScale,
        @Schema(description = "Minimum scale allowed by the client", example = "0.8")
        @DecimalMin(value = "0.1") Double minScale,
        @Schema(description = "Maximum scale allowed by the client", example = "1.4")
        @DecimalMin(value = "0.1") Double maxScale,
        @Schema(description = "Stars defined by vectors from the anchor point")
        @NotEmpty @Valid List<StarRequest> stars,
        @Schema(description = "Edges defined by zero-based indexes of the stars array")
        @Valid List<EdgeRequest> edges
) {

    public record StarRequest(
            @Schema(description = "Relative vector X from the anchor point", example = "-18.5")
            @NotNull Double vectorX,
            @Schema(description = "Relative vector Y from the anchor point", example = "12.0")
            @NotNull Double vectorY
    ) {
    }

    public record EdgeRequest(
            @Schema(description = "Zero-based source star index in the stars array", example = "0")
            @NotNull Integer fromStarIndex,
            @Schema(description = "Zero-based target star index in the stars array", example = "1")
            @NotNull Integer toStarIndex
    ) {
    }
}
