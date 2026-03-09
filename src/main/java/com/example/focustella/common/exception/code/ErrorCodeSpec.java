package com.example.focustella.common.exception.code;

import com.example.focustella.common.api.ApiError;
import org.springframework.http.HttpStatus;

public interface ErrorCodeSpec {

    HttpStatus getStatus();

    String getCode();

    String getMessage();

    default ApiError toApiError(String path) {
        return ApiError.of(getCode(), getMessage(), path);
    }
}
