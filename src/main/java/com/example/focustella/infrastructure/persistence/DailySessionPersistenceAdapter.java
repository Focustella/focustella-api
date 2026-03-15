package com.example.focustella.infrastructure.persistence;

import com.example.focustella.application.port.out.LoadDailySessionPort;
import com.example.focustella.application.port.out.SaveDailySessionPort;
import com.example.focustella.domain.model.ChecklistItem;
import com.example.focustella.domain.model.DailySession;
import com.example.focustella.infrastructure.persistence.entity.ChecklistItemEntity;
import com.example.focustella.infrastructure.persistence.entity.DailySessionEntity;
import com.example.focustella.infrastructure.persistence.repository.DailySessionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailySessionPersistenceAdapter implements SaveDailySessionPort, LoadDailySessionPort {

    private final DailySessionJpaRepository jpaRepository;

    public DailySessionPersistenceAdapter(DailySessionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void saveSession(DailySession dailySession) {
        DailySessionEntity entity = new DailySessionEntity(
                dailySession.getSessionUuid(),
                dailySession.getUserUuid(),
                dailySession.getTimestamp()
        );

        List<ChecklistItemEntity> checklistItems = dailySession.getChecklists().stream()
                .map(item -> new ChecklistItemEntity(item.getTitle(), item.getIsCompleted()))
                .toList();

        entity.setChecklists(checklistItems);

        jpaRepository.save(entity);
    }

    @Override
    public List<DailySession> loadByUserId(String userId) {
        return jpaRepository.findByUserUuidOrderByTimestampDesc(userId)
                .stream()
                .map(entity -> new DailySession(
                        entity.getSessionUuid(),
                        entity.getUserUuid(),
                        entity.getTimestamp(),
                        entity.getChecklists().stream()
                                .map(item -> new ChecklistItem(item.getItemUuid(), item.getTitle(), item.getIsCompleted()))
                                .toList()
                ))
                .toList();
    }
}