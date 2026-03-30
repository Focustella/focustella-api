package com.example.focustella.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LiveActivityErrorCode implements ErrorCodeSpec {
    SLOT_SECONDS_MISMATCH(HttpStatus.BAD_REQUEST, "LIVE_ACTIVITY_400_1", "집중 세션 시간이 일치하지 않습니다."),
    SESSION_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "LIVE_ACTIVITY_400_2", "이미 종료된 집중 세션에는 Live Activity를 등록할 수 없습니다."),
    REGISTRATION_NOT_FOUND(HttpStatus.NOT_FOUND, "LIVE_ACTIVITY_404_1", "Live Activity 등록 정보를 찾을 수 없습니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "LIVE_ACTIVITY_400_3", "Live Activity 상태가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
