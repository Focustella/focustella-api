package com.example.focustella.api.dto.request;

public record FriendAcceptRequest(
        String relationId,
        boolean accept
) {}
