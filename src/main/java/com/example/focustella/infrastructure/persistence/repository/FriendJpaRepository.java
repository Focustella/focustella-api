package com.example.focustella.infrastructure.persistence.repository;

import com.example.focustella.infrastructure.persistence.entity.FriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendJpaRepository extends JpaRepository<FriendEntity, String> {
    List<FriendEntity> findByRequesterIdOrReceiverId(String requesterId, String receiverId);
    Optional<FriendEntity> findByRequesterIdAndReceiverId(String requesterId, String receiverId);
}
