package com.example.focustella.api.docs;

import com.example.focustella.api.dto.response.AnonymousAuthResponse;
import com.example.focustella.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "auth-controller", description = "Authentication API")
public interface AuthControllerDocs {

    @Operation(
            summary = "Create anonymous user token",
            description = "Creates an anonymous user and returns a JWT access token.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Created",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "success": true,
                                                      "data": {
                                                        "accessToken": "jwt-token",
                                                        "user": {
                                                          "id": "4d7d9d91-8ad6-47dd-a37d-8f67f2bce8d1",
                                                          "seed": 123456789,
                                                          "type": "ANONYMOUS",
                                                          "createdAt": "2026-03-19T16:00:00"
                                                        }
                                                      },
                                                      "error": null,
                                                      "timestamp": "2026-03-19T07:00:00Z"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<ApiResponse<AnonymousAuthResponse>> anonymousLogin();
}
