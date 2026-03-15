package com.example.focustella.infrastructure.persistence.repository;

import com.example.focustella.infrastructure.persistence.entity.DailySessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DailySessionJpaRepository extends JpaRepository<DailySessionEntity, String> {
    // 🔥 사용자 UUID로 조회하되, 최신 순(내림차순)으로 정렬하여 가져옵니다.
    List<DailySessionEntity> findByUserUuidOrderByTimestampDesc(String userUuid);
}