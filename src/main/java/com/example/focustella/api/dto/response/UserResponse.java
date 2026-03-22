package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.User;
import com.example.focustella.domain.model.UserType;
import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String userCode,
        String nickname,
        String email, // 이메일 반환 추가 (password는 절대 추가 금지)
        Long seed,
        UserType type,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.id(),
                user.userCode(),
                user.nickname(),
                user.email(),
                user.seed(),
                user.type(),
                user.createdAt()
        );
    }
}
