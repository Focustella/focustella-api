package com.example.focustella.infrastructure.persistence.entity;

import com.example.focustella.domain.model.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "app_user")
public class UserEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "user_code", nullable = false, unique = true, length = 8)
    private String userCode;

    @Column(name = "nickname", nullable = true, length = 255)
    private String nickname;

    @Column(name = "email", nullable = true, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private Long seed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserEntity(String id, String userCode, String nickname, String email, Long seed, UserType type) {
        this.id = id;
        this.userCode = userCode;
        this.nickname = nickname;
        this.email = email;
        this.seed = seed;
        this.type = type;
    }

    public UserEntity(String id, String userCode, String nickname, String email, Long seed, UserType type, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userCode = userCode;
        this.nickname = nickname;
        this.email = email;
        this.seed = seed;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            LocalDateTime now = LocalDateTime.now();
            this.createdAt = now;
            this.updatedAt = now;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
