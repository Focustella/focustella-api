package com.example.focustella.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FocusSessionErrorCode implements ErrorCodeSpec {
    RULE_NOT_FOUND(HttpStatus.BAD_REQUEST, "FOCUS_SESSION_400_1", "집중 시간에 맞는 별자리 규칙이 없습니다."),
    CONSTELLATION_EXHAUSTED(HttpStatus.NOT_FOUND, "FOCUS_SESSION_404_1", "아직 사용하지 않은 별자리를 찾을 수 없습니다."),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "FOCUS_SESSION_404_2", "집중 세션을 찾을 수 없습니다."),
    CONSTELLATION_MISMATCH(HttpStatus.BAD_REQUEST, "FOCUS_SESSION_400_2", "집중 세션의 별자리 정보가 일치하지 않습니다."),
    CONSTELLATION_NOT_FOUND(HttpStatus.NOT_FOUND, "FOCUS_SESSION_404_3", "별자리를 찾을 수 없습니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "FOCUS_SESSION_400_3", "집중 세션 상태가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
