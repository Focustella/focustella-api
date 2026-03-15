package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.ChecklistItem;
import java.time.LocalDateTime;
import java.util.List;

public interface SaveDailySessionUseCase {
    void save(LocalDateTime timestamp, List<ChecklistItem> checklists);
}