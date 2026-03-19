package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.UserTag;

public record UserTagResponse(
        Long id,
        String name
) {
    public static UserTagResponse from(UserTag userTag) {
        return new UserTagResponse(userTag.id(), userTag.name());
    }
}
