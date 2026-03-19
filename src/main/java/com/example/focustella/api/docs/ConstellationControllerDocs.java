package com.example.focustella.api.docs;

import com.example.focustella.api.dto.request.ConstellationCreateRequest;
import com.example.focustella.api.dto.response.ConstellationResponse;
import com.example.focustella.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "constellation-controller", description = "Constellation management API")
public interface ConstellationControllerDocs {

    @Operation(
            summary = "Create constellation",
            description = "Creates a constellation with stars defined by relative vectors and edges defined by star indexes.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ConstellationCreateRequest.class),
                            examples = @ExampleObject(
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
                                    examples = @ExampleObject(
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
                                                        "createdAt": "2026-03-19T16:00:00",
                                                        "updatedAt": "2026-03-19T16:00:00",
                                                        "stars": [
                                                          { "id": 10, "vectorX": -18.5, "vectorY": 12.0 }
                                                        ],
                                                        "edges": [
                                                          { "id": 100, "fromStarId": 10, "toStarId": 11 }
                                                        ]
                                                      },
                                                      "error": null,
                                                      "timestamp": "2026-03-19T07:00:00Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid constellation request",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": "COMMON_400",
                                                        "message": "잘못된 요청입니다.",
                                                        "path": "/api/v1/constellation",
                                                        "details": [
                                                          {
                                                            "field": "name",
                                                            "rejectedValue": null,
                                                            "reason": "must not be blank"
                                                          }
                                                        ]
                                                      },
                                                      "timestamp": "2026-03-19T07:00:00Z"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<ConstellationResponse>> create(ConstellationCreateRequest request);
}
