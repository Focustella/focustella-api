package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.DailySession;
import java.util.List;

public interface GetDailySessionUseCase {
    List<DailySession> getSessionsByUserId(String userId);
}