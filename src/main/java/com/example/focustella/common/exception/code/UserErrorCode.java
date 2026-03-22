package com.example.focustella.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCodeSpec {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_1", "사용자를 찾을 수 없습니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "USER_400_1", "닉네임 형식이 올바르지 않거나 10자를 초과할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
