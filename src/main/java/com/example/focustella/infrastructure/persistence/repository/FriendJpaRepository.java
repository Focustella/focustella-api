package com.example.focustella.infrastructure.persistence.repository;

import com.example.focustella.infrastructure.persistence.entity.FriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendJpaRepository extends JpaRepository<FriendEntity, String> {
    List<FriendEntity> findByRequesterIdOrReceiverId(String requesterId, String receiverId);
    Optional<FriendEntity> findByRequesterIdAndReceiverId(String requesterId, String receiverId);

    @Query("SELECT count(f) > 0 FROM FriendEntity f WHERE " +
           "(f.requesterId = :requesterId AND f.receiverId = :receiverId) OR " +
           "(f.requesterId = :receiverId AND f.receiverId = :requesterId)")
    boolean existsRelation(@Param("requesterId") String requesterId, @Param("receiverId") String receiverId);
}