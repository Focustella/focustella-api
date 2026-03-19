package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.UserTag;

public interface AddFocusTagUseCase {
    UserTag add(String userId, String tagName);
}
