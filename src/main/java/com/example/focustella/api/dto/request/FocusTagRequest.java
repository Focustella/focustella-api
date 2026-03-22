package com.example.focustella.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FocusTagRequest(
        @NotBlank String name
) {
}
