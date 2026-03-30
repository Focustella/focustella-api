package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.FocusLiveActivityStatus;

public record LiveActivityStatusResponse(
        String focusSessionId,
        FocusLiveActivityStatus status
) {
}
