package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.FocusSession;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LoadFocusSessionPort {
    Set<Long> loadUsedConstellationIdsByUserId(String userId);

    Optional<FocusSession> loadByIdAndUserId(String sessionId, String userId);

    List<FocusSession> loadByUserId(String userId);
}
