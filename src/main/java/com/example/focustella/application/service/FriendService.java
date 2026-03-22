package com.example.focustella.application.service;

import com.example.focustella.application.port.in.FriendUseCase;
import com.example.focustella.application.port.out.LoadFriendPort;
import com.example.focustella.application.port.out.SaveFriendPort;
import com.example.focustella.domain.model.FriendRelation;
import com.example.focustella.domain.model.FriendStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendService implements FriendUseCase {

    private final SaveFriendPort saveFriendPort;
    private final LoadFriendPort loadFriendPort;

    public FriendService(SaveFriendPort saveFriendPort, LoadFriendPort loadFriendPort) {
        this.saveFriendPort = saveFriendPort;
        this.loadFriendPort = loadFriendPort;
    }

    @Override
    @Transactional
    public FriendRelation requestFriend(String requesterId, String receiverId) {
        // 중복 요청 방지 로직 필요시 추가
        FriendRelation newRelation = new FriendRelation(null, requesterId, receiverId, FriendStatus.PENDING);
        return saveFriendPort.save(newRelation);
    }

    @Override
    @Transactional
    public FriendRelation acceptFriendRequest(String relationId) {
        FriendRelation relation = loadFriendPort.loadFriendRelationById(relationId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));
        relation.accept();
        return saveFriendPort.save(relation);
    }

    @Override
    @Transactional
    public void rejectFriendRequest(String relationId) {
        FriendRelation relation = loadFriendPort.loadFriendRelationById(relationId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));
        relation.reject();
        saveFriendPort.save(relation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRelation> getFriends(String userId) {
        return loadFriendPort.loadFriends(userId);
    }
}
