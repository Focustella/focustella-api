package com.example.focustella.application.service;

import com.example.focustella.application.port.in.FriendUseCase;
import com.example.focustella.application.port.out.LoadFriendPort;
import com.example.focustella.application.port.out.SaveFriendPort;
import com.example.focustella.common.exception.BusinessException;
import com.example.focustella.common.exception.code.FriendErrorCode;
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
        // 자기 자신에게 친구 요청 방지
        if (requesterId.equals(receiverId)) {
            throw new BusinessException(FriendErrorCode.SELF_REQUEST_NOT_ALLOWED);
        }

        // 이미 친구이거나 요청 대기 중인지 확인 (방향 무관하게 확인)
        if (loadFriendPort.existsRelation(requesterId, receiverId)) {
            throw new BusinessException(FriendErrorCode.ALREADY_FRIEND_OR_PENDING);
        }

        FriendRelation newRelation = new FriendRelation(null, requesterId, receiverId, FriendStatus.PENDING);
        return saveFriendPort.save(newRelation);
    }

    @Override
    @Transactional
    public FriendRelation acceptFriendRequest(String relationId) {
        FriendRelation relation = loadFriendPort.loadFriendRelationById(relationId)
                .orElseThrow(() -> new BusinessException(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND));
        relation.accept();
        return saveFriendPort.save(relation);
    }

    @Override
    @Transactional
    public void rejectFriendRequest(String relationId) {
        FriendRelation relation = loadFriendPort.loadFriendRelationById(relationId)
                .orElseThrow(() -> new BusinessException(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND));
        relation.reject();
        saveFriendPort.save(relation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRelation> getFriends(String userId) {
        return loadFriendPort.loadFriends(userId);
    }
}
