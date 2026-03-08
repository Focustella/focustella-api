package com.example.focustella.common.api;

import java.util.List;

public record ApiError(
        String code,
        String message,
        String path,
        List<FieldErrorDetail> details
) {

    public static ApiError of(String code, String message, String path) {
        return new ApiError(code, message, path, List.of());
    }
}
