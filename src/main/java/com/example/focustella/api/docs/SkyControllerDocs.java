package com.example.focustella.api.docs;

import com.example.focustella.api.dto.response.SkyResponse;
import com.example.focustella.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "sky-controller", description = "Sky API")
public interface SkyControllerDocs {

    @Operation(
            summary = "Get public sky",
            description = "Returns the sky data for the given user id.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "data": {
                                                        "ownerId": "user-id",
                                                        "seed": 123456789,
                                                        "dailyStars": [
                                                          {
                                                            "sessionId": "daily-session-uuid",
                                                            "session": {
                                                              "sessionId": "daily-session-uuid",
                                                              "userId": "user-id",
                                                              "timestamp": "2026-03-18T09:00:00",
                                                              "checklists": [
                                                                {
                                                                  "itemUuid": "item-uuid",
                                                                  "title": "Drink water",
                                                                  "isCompleted": true
                                                                }
                                                              ]
                                                            }
                                                          }
                                                        ],
                                                        "focusConstellations": [
                                                          {
                                                            "sessionId": "focus-session-uuid",
                                                            "constellationId": 1,
                                                            "durationMinutes": 50,
                                                            "startedAt": "2026-03-18T01:00:00Z",
                                                            "endedAt": "2026-03-18T01:25:00Z",
                                                            "slotSeconds": 1500,
                                                            "discoveredStarCount": 12,
                                                            "topicTags": ["iOS", "디버깅"],
                                                            "rating": 4,
                                                            "freeText": "상태 전이 점검",
                                                            "constellation": {
                                                              "id": 1,
                                                              "name": "Orion Starter",
                                                              "createdBy": null,
                                                              "starCount": 5,
                                                              "defaultScale": 1.0,
                                                              "minScale": 0.8,
                                                              "maxScale": 1.4,
                                                              "createdAt": "2026-03-18T00:00:00",
                                                              "updatedAt": "2026-03-18T00:00:00",
                                                              "stars": [],
                                                              "edges": []
                                                            }
                                                          }
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
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": "COMMON_404",
                                                        "message": "요청한 리소스를 찾을 수 없습니다.",
                                                        "path": "/api/v1/sky/user-id",
                                                        "details": []
                                                      },
                                                      "timestamp": "2026-03-19T07:00:00Z"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<SkyResponse>> getSky(String id);

    @Operation(
            summary = "Get my sky",
            description = "Returns the sky data for the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "data": {
                                                        "ownerId": "user-id",
                                                        "seed": 123456789,
                                                        "dailyStars": [],
                                                        "focusConstellations": []
                                                      },
                                                      "error": null,
                                                      "timestamp": "2026-03-19T07:00:00Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": "COMMON_401",
                                                        "message": "인증이 필요합니다.",
                                                        "path": "/api/v1/sky/me",
                                                        "details": []
                                                      },
                                                      "timestamp": "2026-03-19T07:00:00Z"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<SkyResponse>> getMySky(Authentication authentication);
}
