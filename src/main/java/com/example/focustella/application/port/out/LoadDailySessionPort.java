package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.DailySession;
import java.util.List;

public interface LoadDailySessionPort {
    List<DailySession> loadByUserId(String userId);
}