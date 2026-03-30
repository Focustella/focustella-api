package com.example.focustella.infrastructure.persistence.repository;

import com.example.focustella.domain.model.FocusLiveActivityStatus;
import com.example.focustella.infrastructure.persistence.entity.FocusLiveActivityEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FocusLiveActivityJpaRepository extends JpaRepository<FocusLiveActivityEntity, Long> {

    Optional<FocusLiveActivityEntity> findByFocusSessionId(String focusSessionId);

    List<FocusLiveActivityEntity> findByStatus(FocusLiveActivityStatus status);
}
