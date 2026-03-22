package com.example.focustella.api.dto.response;

public record SignInResponse(
        String accessToken,
        UserResponse user
) {
}
