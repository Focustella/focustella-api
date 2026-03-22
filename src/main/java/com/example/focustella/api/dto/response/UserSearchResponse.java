package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.User;

public record UserSearchResponse(
        String id,
        String userCode,
        String nickname
) {
    public static UserSearchResponse from(User user) {
        return new UserSearchResponse(
                user.id(),
                user.userCode(),
                user.nickname()
        );
    }
}
