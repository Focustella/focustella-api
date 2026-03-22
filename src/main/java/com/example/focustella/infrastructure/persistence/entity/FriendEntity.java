package com.example.focustella.infrastructure.persistence.entity;

import com.example.focustella.domain.model.FriendStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "friend_relation")
public class FriendEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @Column(name = "requester_id", nullable = false, length = 36)
    private String requesterId;

    @Column(name = "receiver_id", nullable = false, length = 36)
    private String receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendStatus status;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public FriendEntity(String id, String requesterId, String receiverId, FriendStatus status) {
        this.id = id;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.status = status;
    }

    public void updateStatus(FriendStatus status) {
        this.status = status;
    }
}
