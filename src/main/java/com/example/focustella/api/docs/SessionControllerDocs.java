package com.example.focustella.api.docs;

import com.example.focustella.api.dto.request.DailySessionSaveRequest;
import com.example.focustella.api.dto.request.FocusSessionSaveRequest;
import com.example.focustella.api.dto.request.FocusTagRequest;
import com.example.focustella.api.dto.response.DailySessionResponse;
import com.example.focustella.api.dto.response.FocusSessionCreateResponse;
import com.example.focustella.api.dto.response.FocusSessionResponse;
import com.example.focustella.api.dto.response.UserTagResponse;
import com.example.focustella.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

@Tag(name = "session-controller", description = "Session API")
public interface SessionControllerDocs {

    @Operation(
            summary = "Save daily session",
            description = "Saves a completed daily session for the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DailySessionSaveRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2026-03-18T09:00:00",
                                              "checklists": [
                                                { "title": "Drink water", "isCompleted": true }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Saved",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "data": null,
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
                                                        "path": "/api/v1/session/daily",
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
    ResponseEntity<ApiResponse<Void>> saveDailySession(Authentication authentication, DailySessionSaveRequest request);

    @Operation(
            summary = "Get daily sessions",
            description = "Returns daily sessions for the authenticated user.",
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
                                                      "data": [
                                                        {
                                                          "timestamp": "2026-03-18T09:00:00Z",
                                                          "checklists": [
                                                            {
                                                              "itemUuid": "item-uuid",
                                                              "title": "Drink water",
                                                              "isCompleted": true
                                                            }
                                                          ]
                                                        }
                                                      ],
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
                                                        "path": "/api/v1/session/daily",
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
    ResponseEntity<ApiResponse<List<DailySessionResponse>>> getDailySessions(Authentication authentication);

    @Operation(
            summary = "Get focus session constellation",
            description = "Returns the constellation to use when starting a focus session and creates a planned focus session record.",
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
                                                        "focusSessionId": "b1fdb085-1e47-4747-a7df-4814ba4e25cd",
                                                        "constellationId": 1,
                                                        "durationMinutes": 50,
                                                        "minStarCount": 4,
                                                        "maxStarCount": 6,
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
                                                          "stars": [
                                                            { "id": 10, "vectorX": -18.5, "vectorY": 12.0 },
                                                            { "id": 11, "vectorX": -4.0, "vectorY": 3.5 }
                                                          ],
                                                          "edges": [
                                                            { "id": 100, "fromStarId": 10, "toStarId": 11 }
                                                          ]
                                                        }
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
                            description = "Invalid duration or rule",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": "COMMON_400",
                                                        "message": "잘못된 요청입니다.",
                                                        "path": "/api/v1/session/focus/create",
                                                        "details": [
                                                          {
                                                            "field": "durationMinutes",
                                                            "rejectedValue": "abc",
                                                            "reason": "요청 파라미터 타입이 올바르지 않습니다."
                                                          }
                                                        ]
                                                      },
                                                      "timestamp": "2026-03-19T07:00:00Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No available constellation",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": "FOCUS_SESSION_404_1",
                                                        "message": "아직 사용하지 않은 별자리를 찾을 수 없습니다.",
                                                        "path": "/api/v1/session/focus/create",
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
    ResponseEntity<ApiResponse<FocusSessionCreateResponse>> createFocusSession(
            Authentication authentication,
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "durationMinutes",
                    description = "Requested focus duration in minutes",
                    example = "50",
                    required = true
            )
            Integer durationMinutes
    );

    @Operation(
            summary = "Save focus session",
            description = "Completes a planned focus session with the final client payload.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = FocusSessionSaveRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "sessionId": "uuid",
                                              "constellationId": 1,
                                              "startedAt": "2026-03-18T01:00:00Z",
                                              "endedAt": "2026-03-18T01:25:00Z",
                                              "slotSeconds": 1500,
                                              "discoveredStarCount": 12,
                                              "topicTags": ["iOS", "디버깅"],
                                              "rating": 4,
                                              "freeText": "상태 전이 점검"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Saved",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "data": {
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
                                                        "status": "COMPLETED",
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
                            description = "Invalid state or constellation mismatch",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": "FOCUS_SESSION_400_2",
                                                        "message": "집중 세션의 별자리 정보가 일치하지 않습니다.",
                                                        "path": "/api/v1/session/focus",
                                                        "details": []
                                                      },
                                                      "timestamp": "2026-03-19T07:00:00Z"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Focus session or constellation not found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": "FOCUS_SESSION_404_2",
                                                        "message": "집중 세션을 찾을 수 없습니다.",
                                                        "path": "/api/v1/session/focus",
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
    ResponseEntity<ApiResponse<FocusSessionResponse>> saveFocusSession(Authentication authentication, FocusSessionSaveRequest request);

    @Operation(
            summary = "Get focus sessions",
            description = "Returns completed focus sessions for the authenticated user, including constellation details.",
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
                                                      "data": [
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
                                                          "status": "COMPLETED",
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
                                                      ],
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
                                                        "path": "/api/v1/session/focus",
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
    ResponseEntity<ApiResponse<List<FocusSessionResponse>>> getFocusSessions(Authentication authentication);

    @Operation(
            summary = "Add focus tag",
            description = "Adds a user-owned focus tag. If the tag already exists for the user, returns the existing tag.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = FocusTagRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "name": "iOS"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Added",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "data": {
                                                        "id": 1,
                                                        "name": "iOS"
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
                            description = "Invalid tag name",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": "COMMON_400",
                                                        "message": "잘못된 요청입니다.",
                                                        "path": "/api/v1/session/focus/tag/add",
                                                        "details": [
                                                          {
                                                            "field": "name",
                                                            "rejectedValue": "",
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
    ResponseEntity<ApiResponse<UserTagResponse>> addFocusTag(Authentication authentication, FocusTagRequest request);

    @Operation(
            summary = "Delete focus tag",
            description = "Deletes a user-owned focus tag by name.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = FocusTagRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "name": "iOS"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Deleted",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "data": {
                                                        "id": 1,
                                                        "name": "iOS"
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
                            description = "Tag not found",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": "USER_TAG_404_1",
                                                        "message": "태그를 찾을 수 없습니다.",
                                                        "path": "/api/v1/session/focus/tag/delete",
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
    ResponseEntity<ApiResponse<UserTagResponse>> deleteFocusTag(Authentication authentication, FocusTagRequest request);

    @Operation(
            summary = "Get focus tags",
            description = "Returns all user-owned focus tags.",
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
                                                      "data": [
                                                        { "id": 1, "name": "iOS" },
                                                        { "id": 2, "name": "디버깅" }
                                                      ],
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
                                                        "path": "/api/v1/session/focus/tag",
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
    ResponseEntity<ApiResponse<List<UserTagResponse>>> getFocusTags(Authentication authentication);
}
