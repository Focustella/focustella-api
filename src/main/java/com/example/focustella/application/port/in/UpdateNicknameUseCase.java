package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.User;

public interface UpdateNicknameUseCase {
    User updateNickname(String userId, String nickname);
}
