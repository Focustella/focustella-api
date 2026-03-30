package com.example.focustella.api.dto.request;

import com.example.focustella.domain.model.FocusLiveActivityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public record LiveActivityRegisterRequest(
        @NotBlank String focusSessionId,
        @NotBlank String activityId,
        @NotBlank String pushToken,
        @NotNull Instant startedAt,
        @NotNull @Positive Integer slotSeconds,
        @NotNull FocusLiveActivityStatus status,
        @NotNull Long constellationId,
        @NotNull Double rotationRadians
) {
}
