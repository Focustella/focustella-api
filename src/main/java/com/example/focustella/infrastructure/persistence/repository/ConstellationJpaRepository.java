package com.example.focustella.infrastructure.persistence.repository;

import com.example.focustella.infrastructure.persistence.entity.ConstellationEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstellationJpaRepository extends JpaRepository<ConstellationEntity, Long> {

    boolean existsByNameAndDeletedAtIsNull(String name);

    List<ConstellationEntity> findByDeletedAtIsNullAndStarCountBetween(int minStarCount, int maxStarCount);

    Optional<ConstellationEntity> findByIdAndDeletedAtIsNull(Long id);
}
