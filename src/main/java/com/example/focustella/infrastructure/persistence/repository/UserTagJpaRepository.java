package com.example.focustella.infrastructure.persistence.repository;

import com.example.focustella.infrastructure.persistence.entity.UserTagEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTagJpaRepository extends JpaRepository<UserTagEntity, Long> {
    Optional<UserTagEntity> findByUserIdAndNormalizedName(String userId, String normalizedName);

    List<UserTagEntity> findByUserIdOrderByNameAsc(String userId);
}
