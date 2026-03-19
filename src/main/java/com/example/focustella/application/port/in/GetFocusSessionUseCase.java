package com.example.focustella.application.port.in;

import com.example.focustella.api.dto.response.FocusSessionResponse;
import java.util.List;

public interface GetFocusSessionUseCase {
    List<FocusSessionResponse> getCompletedSessions(String userId);
}
