package com.example.focustella.infrastructure.persistence.repository;

import com.example.focustella.infrastructure.persistence.entity.ConstellationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstellationJpaRepository extends JpaRepository<ConstellationEntity, Long> {

    boolean existsByNameAndDeletedAtIsNull(String name);
}
