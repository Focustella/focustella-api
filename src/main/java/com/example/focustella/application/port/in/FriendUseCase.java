package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.FriendRelation;
import java.util.List;

public interface FriendUseCase {
    FriendRelation requestFriend(String requesterId, String receiverId);
    FriendRelation acceptFriendRequest(String relationId);
    void rejectFriendRequest(String relationId);
    List<FriendRelation> getFriends(String userId);
}
