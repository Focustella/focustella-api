package com.example.focustella.api.dto.response;

public record LiveActivityRegisterResponse(
        String focusSessionId,
        boolean registered
) {
}
