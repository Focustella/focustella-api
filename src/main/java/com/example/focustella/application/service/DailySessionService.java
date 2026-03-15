package com.example.focustella.application.service;

import com.example.focustella.application.port.in.GetDailySessionUseCase;
import com.example.focustella.application.port.in.SaveDailySessionUseCase;
import com.example.focustella.application.port.out.LoadDailySessionPort;
import com.example.focustella.application.port.out.SaveDailySessionPort;
import com.example.focustella.domain.model.ChecklistItem;
import com.example.focustella.domain.model.DailySession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DailySessionService implements SaveDailySessionUseCase, GetDailySessionUseCase {

    private final SaveDailySessionPort saveDailySessionPort;
    private final LoadDailySessionPort loadDailySessionPort;

    public DailySessionService(SaveDailySessionPort saveDailySessionPort, LoadDailySessionPort loadDailySessionPort) {
        this.saveDailySessionPort = saveDailySessionPort;
        this.loadDailySessionPort = loadDailySessionPort;
    }

    @Override
    @Transactional
    public void save(LocalDateTime timestamp, List<ChecklistItem> checklists) {
        // 요구사항: UUID류는 더미 데이터로 처리
        String dummySessionUuid = UUID.randomUUID().toString();
        String dummyUserUuid = "dummy-user-uuid-1234";

        DailySession session = new DailySession(
                dummySessionUuid,
                dummyUserUuid,
                timestamp,
                checklists
        );

        saveDailySessionPort.saveSession(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailySession> getSessionsByUserId(String userId) {
        return loadDailySessionPort.loadByUserId(userId);
    }
}