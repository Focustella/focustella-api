package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.UserTag;

public interface DeleteFocusTagUseCase {
    UserTag delete(String userId, String tagName);
}
