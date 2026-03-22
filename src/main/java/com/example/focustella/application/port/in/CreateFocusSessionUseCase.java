package com.example.focustella.application.port.in;

import com.example.focustella.api.dto.response.FocusSessionCreateResponse;

public interface CreateFocusSessionUseCase {
    FocusSessionCreateResponse prepare(String userId, Integer durationMinutes);
}
