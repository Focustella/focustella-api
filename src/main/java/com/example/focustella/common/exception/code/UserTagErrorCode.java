package com.example.focustella.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserTagErrorCode implements ErrorCodeSpec {
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_TAG_404_1", "태그를 찾을 수 없습니다."),
    INVALID_NAME(HttpStatus.BAD_REQUEST, "USER_TAG_400_1", "태그 이름이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
