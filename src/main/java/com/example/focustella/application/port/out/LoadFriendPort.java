package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.FriendRelation;

import java.util.List;
import java.util.Optional;

public interface LoadFriendPort {
    List<FriendRelation> loadFriends(String userId);
    Optional<FriendRelation> loadFriendRelation(String requesterId, String receiverId);
    Optional<FriendRelation> loadFriendRelationById(String relationId);
}
