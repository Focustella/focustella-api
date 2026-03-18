package com.example.focustella.application.service;

import com.example.focustella.application.port.in.GetDailySessionUseCase;
import com.example.focustella.application.port.in.SaveDailySessionUseCase;
import com.example.focustella.application.port.out.LoadDailySessionPort;
import com.example.focustella.application.port.out.LoadUserPort;
import com.example.focustella.application.port.out.SaveDailySessionPort;
import com.example.focustella.common.exception.BusinessException;
import com.example.focustella.common.exception.code.CommonErrorCode;
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
    private final LoadUserPort loadUserPort;

    public DailySessionService(
            SaveDailySessionPort saveDailySessionPort,
            LoadDailySessionPort loadDailySessionPort,
            LoadUserPort loadUserPort
    ) {
        this.saveDailySessionPort = saveDailySessionPort;
        this.loadDailySessionPort = loadDailySessionPort;
        this.loadUserPort = loadUserPort;
    }

    @Override
    @Transactional
    public void save(LocalDateTime timestamp, List<ChecklistItem> checklists, String authenticatedUserId) {
        loadUserPort.loadById(authenticatedUserId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String dummySessionUuid = UUID.randomUUID().toString();

        DailySession session = new DailySession(
                dummySessionUuid,
                authenticatedUserId,
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
