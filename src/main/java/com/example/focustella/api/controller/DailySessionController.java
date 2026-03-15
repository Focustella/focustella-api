package com.example.focustella.api.controller;

import com.example.focustella.api.dto.request.DailySessionSaveRequest;
import com.example.focustella.api.dto.response.DailySessionResponse;
import com.example.focustella.application.port.in.GetDailySessionUseCase;
import com.example.focustella.application.port.in.SaveDailySessionUseCase;
import com.example.focustella.domain.model.ChecklistItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/session")
public class DailySessionController {

    private final SaveDailySessionUseCase saveDailySessionUseCase;
    private final GetDailySessionUseCase getDailySessionUseCase;

    public DailySessionController(SaveDailySessionUseCase saveDailySessionUseCase, GetDailySessionUseCase getDailySessionUseCase) {
        this.saveDailySessionUseCase = saveDailySessionUseCase;
        this.getDailySessionUseCase = getDailySessionUseCase;
    }

    @PostMapping("/daily")
    public ResponseEntity<Void> saveDailySession(@RequestBody DailySessionSaveRequest request) {
        List<ChecklistItem> items = request.checklists() == null ? List.of() :
                request.checklists().stream()
                        .map(item -> new ChecklistItem(null, item.title(), item.isCompleted()))
                        .toList();

        saveDailySessionUseCase.save(request.timestamp(), items);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailySessionResponse>> getDailySessions(@RequestParam("userId") String userId) {
        List<DailySessionResponse> responses = getDailySessionUseCase.getSessionsByUserId(userId)
                .stream()
                .map(DailySessionResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }
}