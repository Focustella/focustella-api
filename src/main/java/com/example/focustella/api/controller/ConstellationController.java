package com.example.focustella.api.controller;

import com.example.focustella.api.dto.request.ConstellationCreateRequest;
import com.example.focustella.api.dto.response.ConstellationResponse;
import com.example.focustella.application.port.in.CreateConstellationUseCase;
import com.example.focustella.common.api.ApiResponse;
import com.example.focustella.domain.model.Constellation;
import com.example.focustella.domain.model.ConstellationEdgeDraft;
import com.example.focustella.domain.model.ConstellationStarDraft;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/constellations")
@Tag(name = "Constellation", description = "Constellation management API")
public class ConstellationController {

    private final CreateConstellationUseCase createConstellationUseCase;

    public ConstellationController(CreateConstellationUseCase createConstellationUseCase) {
        this.createConstellationUseCase = createConstellationUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Create constellation",
            description = "Creates a constellation with stars defined by relative vectors and edges defined by star indexes.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConstellationCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "createConstellation",
                                    value = """
                                            {
                                              "name": "Orion Starter",
                                              "createdBy": null,
                                              "defaultScale": 1.0,
                                              "minScale": 0.8,
                                              "maxScale": 1.4,
                                              "stars": [
                                                { "vectorX": -18.5, "vectorY": 12.0 },
                                                { "vectorX": -4.0, "vectorY": 3.5 },
                                                { "vectorX": 10.0, "vectorY": -6.0 }
                                              ],
                                              "edges": [
                                                { "fromStarIndex": 0, "toStarIndex": 1 },
                                                { "fromStarIndex": 1, "toStarIndex": 2 }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Constellation created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.example.focustella.common.api.ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "successResponse",
                                            value = """
                                                    {
                                                      "success": true,
                                                      "data": {
                                                        "id": 1,
                                                        "name": "Orion Starter",
                                                        "createdBy": null,
                                                        "starCount": 3,
                                                        "defaultScale": 1.0,
                                                        "minScale": 0.8,
                                                        "maxScale": 1.4,
                                                        "createdAt": "2026-03-17T23:00:00",
                                                        "updatedAt": "2026-03-17T23:00:00",
                                                        "stars": [
                                                          { "id": 10, "vectorX": -18.5, "vectorY": 12.0 },
                                                          { "id": 11, "vectorX": -4.0, "vectorY": 3.5 },
                                                          { "id": 12, "vectorX": 10.0, "vectorY": -6.0 }
                                                        ],
                                                        "edges": [
                                                          { "id": 100, "fromStarId": 10, "toStarId": 11 },
                                                          { "id": 101, "fromStarId": 11, "toStarId": 12 }
                                                        ]
                                                      },
                                                      "error": null,
                                                      "timestamp": "2026-03-17T14:00:00Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. Possible constellation-specific error codes: CONSTELLATION_400_1, CONSTELLATION_400_2, CONSTELLATION_400_3, CONSTELLATION_400_4",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = com.example.focustella.common.api.ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "duplicateName",
                                                    value = """
                                                            {
                                                              "success": false,
                                                              "data": null,
                                                              "error": {
                                                                "code": "CONSTELLATION_400_1",
                                                                "message": "이미 존재하는 별자리 이름입니다.",
                                                                "path": "/api/v1/constellations",
                                                                "details": []
                                                              },
                                                              "timestamp": "2026-03-17T14:00:00Z"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "invalidScaleRange",
                                                    value = """
                                                            {
                                                              "success": false,
                                                              "data": null,
                                                              "error": {
                                                                "code": "CONSTELLATION_400_2",
                                                                "message": "defaultScale must be less than or equal to maxScale.",
                                                                "path": "/api/v1/constellations",
                                                                "details": []
                                                              },
                                                              "timestamp": "2026-03-17T14:00:00Z"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "invalidEdgeIndex",
                                                    value = """
                                                            {
                                                              "success": false,
                                                              "data": null,
                                                              "error": {
                                                                "code": "CONSTELLATION_400_3",
                                                                "message": "Edge index is out of range.",
                                                                "path": "/api/v1/constellations",
                                                                "details": []
                                                              },
                                                              "timestamp": "2026-03-17T14:00:00Z"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "invalidEdgeConnection",
                                                    value = """
                                                            {
                                                              "success": false,
                                                              "data": null,
                                                              "error": {
                                                                "code": "CONSTELLATION_400_4",
                                                                "message": "Edge must connect two different stars.",
                                                                "path": "/api/v1/constellations",
                                                                "details": []
                                                              },
                                                              "timestamp": "2026-03-17T14:00:00Z"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<ConstellationResponse>> create(@Valid @RequestBody ConstellationCreateRequest request) {
        Constellation constellation = createConstellationUseCase.create(
                request.name(),
                request.createdBy(),
                request.defaultScale(),
                request.minScale(),
                request.maxScale(),
                request.stars().stream()
                        .map(star -> new ConstellationStarDraft(star.vectorX(), star.vectorY()))
                        .toList(),
                request.edges() == null ? List.of() : request.edges().stream()
                        .map(edge -> new ConstellationEdgeDraft(edge.fromStarIndex(), edge.toStarIndex()))
                        .toList()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ConstellationResponse.from(constellation)));
    }
}
