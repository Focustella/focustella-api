package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.FriendRelation;

public interface SaveFriendPort {
    FriendRelation save(FriendRelation friendRelation);
}
