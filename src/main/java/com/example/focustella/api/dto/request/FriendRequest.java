package com.example.focustella.api.dto.request;

public record FriendRequest(
        String requesterId,
        String receiverId
) {}
