package com.example.focustella.domain.model;

import java.time.LocalDateTime;

public record User(
        String id,
        String userCode, // 8자리 랜덤 문자열 식별번호
        String nickname, // nullable 닉네임
        String email, // nullable 이메일 (익명유저는 null)
        Long seed,
        UserType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
