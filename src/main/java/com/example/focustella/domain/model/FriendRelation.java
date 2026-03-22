package com.example.focustella.domain.model;

import lombok.Getter;

@Getter
public class FriendRelation {
    private final String id;
    private final String requesterId;
    private final String receiverId;
    private FriendStatus status;

    public FriendRelation(String id, String requesterId, String receiverId, FriendStatus status) {
        this.id = id;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.status = status;
    }

    public void accept() {
        this.status = FriendStatus.ACCEPTED;
    }

    public void reject() {
        this.status = FriendStatus.REJECTED;
    }
}
