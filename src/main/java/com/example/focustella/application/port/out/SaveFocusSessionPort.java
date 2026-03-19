package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.FocusSession;

public interface SaveFocusSessionPort {
    FocusSession save(FocusSession focusSession);
}
