package com.example.focustella.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ConstellationErrorCode implements ErrorCodeSpec {
    DUPLICATE_NAME(HttpStatus.BAD_REQUEST, "CONSTELLATION_400_1", "이미 존재하는 별자리 이름입니다."),
    INVALID_SCALE_RANGE(HttpStatus.BAD_REQUEST, "CONSTELLATION_400_2", "별자리 스케일 범위가 올바르지 않습니다."),
    INVALID_EDGE_INDEX(HttpStatus.BAD_REQUEST, "CONSTELLATION_400_3", "별자리 선 연결 정보가 올바르지 않습니다."),
    INVALID_EDGE_CONNECTION(HttpStatus.BAD_REQUEST, "CONSTELLATION_400_4", "같은 별끼리는 연결할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
