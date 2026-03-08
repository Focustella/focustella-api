package com.example.focustella.common.api;

public record FieldErrorDetail(
        String field,
        Object rejectedValue,
        String reason
) {
}
