package com.example.focustella.application.port.in;

import com.example.focustella.api.dto.request.FocusSessionSaveRequest;
import com.example.focustella.api.dto.response.FocusSessionResponse;

public interface SaveFocusSessionUseCase {
    FocusSessionResponse save(String userId, FocusSessionSaveRequest request);
}
