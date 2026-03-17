package com.example.focustella.api.controller;

import com.example.focustella.api.dto.request.DailySessionSaveRequest;
import com.example.focustella.api.dto.response.DailySessionResponse;
import com.example.focustella.application.port.in.GetDailySessionUseCase;
import com.example.focustella.application.port.in.SaveDailySessionUseCase;
import com.example.focustella.common.api.ApiResponse;
import com.example.focustella.domain.model.ChecklistItem;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/session")
public class DailySessionController {

    private final SaveDailySessionUseCase saveDailySessionUseCase;
    private final GetDailySessionUseCase getDailySessionUseCase;

    public DailySessionController(
            SaveDailySessionUseCase saveDailySessionUseCase,
            GetDailySessionUseCase getDailySessionUseCase
    ) {
        this.saveDailySessionUseCase = saveDailySessionUseCase;
        this.getDailySessionUseCase = getDailySessionUseCase;
    }

    @PostMapping("/daily")
    public ResponseEntity<ApiResponse<Void>> saveDailySession(
            Authentication authentication,
            @Valid @RequestBody DailySessionSaveRequest request
    ) {
        List<ChecklistItem> items = request.checklists() == null ? List.of() :
                request.checklists().stream()
                        .map(item -> new ChecklistItem(null, item.title(), item.isCompleted()))
                        .toList();

        saveDailySessionUseCase.save(request.timestamp(), items, authentication.getName());
        return ResponseEntity.ok(ApiResponse.emptySuccess());
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<DailySessionResponse>>> getDailySessions(Authentication authentication) {
        List<DailySessionResponse> responses = getDailySessionUseCase.getSessionsByUserId(authentication.getName())
                .stream()
                .map(DailySessionResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
