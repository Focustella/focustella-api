package com.example.focustella.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FriendErrorCode implements ErrorCodeSpec {
    SELF_REQUEST_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FRIEND_400_1", "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    ALREADY_FRIEND_OR_PENDING(HttpStatus.CONFLICT, "FRIEND_409_1", "이미 친구이거나 친구 요청이 진행 중입니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND_404_1", "존재하지 않는 친구 요청입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
