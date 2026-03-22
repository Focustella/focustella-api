package com.example.focustella.api.dto.response;

import com.example.focustella.domain.model.FriendRelation;
import com.example.focustella.domain.model.FriendStatus;

public record FriendResponse(
        String id,
        String requesterId,
        String receiverId,
        FriendStatus status,
        // 친구의 추가 정보 (친구 목록 조회 시 조인/조회하여 채워넣음)
        String nickname,
        String userCode
) {
    public static FriendResponse from(FriendRelation relation, String nickname, String userCode) {
        return new FriendResponse(
                relation.getId(),
                relation.getRequesterId(),
                relation.getReceiverId(),
                relation.getStatus(),
                nickname,
                userCode
        );
    }
}
