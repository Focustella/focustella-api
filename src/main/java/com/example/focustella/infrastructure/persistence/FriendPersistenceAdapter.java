package com.example.focustella.infrastructure.persistence;

import com.example.focustella.application.port.out.LoadFriendPort;
import com.example.focustella.application.port.out.SaveFriendPort;
import com.example.focustella.domain.model.FriendRelation;
import com.example.focustella.infrastructure.persistence.entity.FriendEntity;
import com.example.focustella.infrastructure.persistence.repository.FriendJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FriendPersistenceAdapter implements SaveFriendPort, LoadFriendPort {

    private final FriendJpaRepository friendJpaRepository;

    public FriendPersistenceAdapter(FriendJpaRepository friendJpaRepository) {
        this.friendJpaRepository = friendJpaRepository;
    }

    @Override
    public FriendRelation save(FriendRelation friendRelation) {
        FriendEntity entity = new FriendEntity(
                friendRelation.getId(),
                friendRelation.getRequesterId(),
                friendRelation.getReceiverId(),
                friendRelation.getStatus()
        );
        FriendEntity savedEntity = friendJpaRepository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public List<FriendRelation> loadFriends(String userId) {
        return friendJpaRepository.findByRequesterIdOrReceiverId(userId, userId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FriendRelation> loadFriendRelation(String requesterId, String receiverId) {
        return friendJpaRepository.findByRequesterIdAndReceiverId(requesterId, receiverId)
                .map(this::mapToDomain);
    }

    @Override
    public Optional<FriendRelation> loadFriendRelationById(String relationId) {
        return friendJpaRepository.findById(relationId)
                .map(this::mapToDomain);
    }

    private FriendRelation mapToDomain(FriendEntity entity) {
        return new FriendRelation(
                entity.getId(),
                entity.getRequesterId(),
                entity.getReceiverId(),
                entity.getStatus()
        );
    }
}
