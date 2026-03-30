package com.example.focustella.api.dto.request;

import com.example.focustella.domain.model.FocusLiveActivityEndReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record LiveActivityEndRequest(
        @NotBlank String focusSessionId,
        @NotNull Instant endedAt,
        @NotNull FocusLiveActivityEndReason reason
) {
}
