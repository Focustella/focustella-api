package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.User;
import com.example.focustella.domain.model.UserType;
import java.time.LocalDateTime;

public record UserResponse(
        String id,
        Long seed,
        UserType type,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.id(), user.seed(), user.type(), user.createdAt());
    }
}
