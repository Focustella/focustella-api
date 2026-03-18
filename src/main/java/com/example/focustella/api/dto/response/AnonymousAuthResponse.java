package com.example.focustella.api.dto.response;

public record AnonymousAuthResponse(
        String accessToken,
        UserResponse user
) {
}
