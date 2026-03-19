package com.example.focustella.infrastructure.persistence.repository;

import com.example.focustella.infrastructure.persistence.entity.FocusSessionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FocusSessionJpaRepository extends JpaRepository<FocusSessionEntity, String> {

    @Query("""
            select distinct fs.constellation.id
            from FocusSessionEntity fs
            where fs.userId = :userId
              and fs.status = com.example.focustella.domain.model.FocusSessionStatus.COMPLETED
            """)
    List<Long> findDistinctConstellationIdsByUserId(String userId);

    Optional<FocusSessionEntity> findByIdAndUserId(String id, String userId);

    List<FocusSessionEntity> findByUserIdOrderByCreatedAtDesc(String userId);
}
