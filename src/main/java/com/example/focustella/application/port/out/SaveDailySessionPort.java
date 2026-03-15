package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.DailySession;

public interface SaveDailySessionPort {
    void saveSession(DailySession dailySession);
}