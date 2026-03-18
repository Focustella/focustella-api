package com.example.focustella.infrastructure.persistence.repository;

import com.example.focustella.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
}
