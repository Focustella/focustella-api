package com.example.focustella.application.service;

import com.example.focustella.application.port.in.GetSkyUseCase;
import com.example.focustella.application.port.out.LoadConstellationPort;
import com.example.focustella.application.port.out.LoadDailySessionPort;
import com.example.focustella.application.port.out.LoadFocusSessionPort;
import com.example.focustella.application.port.out.LoadUserPort;
import com.example.focustella.common.exception.BusinessException;
import com.example.focustella.common.exception.code.CommonErrorCode;
import com.example.focustella.common.exception.code.FocusSessionErrorCode;
import com.example.focustella.domain.model.DailySession;
import com.example.focustella.domain.model.FocusSessionStatus;
import com.example.focustella.domain.model.Sky;
import com.example.focustella.domain.model.SkyDailyStar;
import com.example.focustella.domain.model.SkyFocusConstellation;
import com.example.focustella.domain.model.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkyService implements GetSkyUseCase {

    private final LoadUserPort loadUserPort;
    private final LoadDailySessionPort loadDailySessionPort;
    private final LoadFocusSessionPort loadFocusSessionPort;
    private final LoadConstellationPort loadConstellationPort;

    public SkyService(
            LoadUserPort loadUserPort,
            LoadDailySessionPort loadDailySessionPort,
            LoadFocusSessionPort loadFocusSessionPort,
            LoadConstellationPort loadConstellationPort
    ) {
        this.loadUserPort = loadUserPort;
        this.loadDailySessionPort = loadDailySessionPort;
        this.loadFocusSessionPort = loadFocusSessionPort;
        this.loadConstellationPort = loadConstellationPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Sky getSky(String id) {
        User user = loadUserPort.loadById(id)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<SkyDailyStar> dailyStars = loadDailySessionPort.loadByUserId(user.id()).stream()
                .map(this::toDailyStar)
                .toList();

        List<SkyFocusConstellation> focusConstellations = loadFocusSessionPort.loadByUserId(user.id()).stream()
                .filter(session -> session.status() == FocusSessionStatus.COMPLETED)
                .map(session -> new SkyFocusConstellation(
                        session,
                        loadConstellationPort.loadById(session.constellationId())
                                .orElseThrow(() -> new BusinessException(FocusSessionErrorCode.CONSTELLATION_NOT_FOUND))
                ))
                .toList();

        return new Sky(user.id(), user.seed(), dailyStars, focusConstellations);
    }

    private SkyDailyStar toDailyStar(DailySession session) {
        return new SkyDailyStar(
                session.getSessionUuid(),
                session
        );
    }
}
