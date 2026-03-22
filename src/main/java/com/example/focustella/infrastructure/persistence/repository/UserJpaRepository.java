package com.example.focustella.infrastructure.persistence.repository;

import com.example.focustella.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserCode(String userCode);
    
    // 닉네임 일부 검색 (LIKE %keyword%)
    List<UserEntity> findByNicknameContainingIgnoreCase(String nickname);
}