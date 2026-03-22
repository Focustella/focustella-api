package com.example.focustella.api.controller;

import com.example.focustella.api.docs.SessionControllerDocs;
import com.example.focustella.api.dto.request.DailySessionSaveRequest;
import com.example.focustella.api.dto.request.FocusSessionSaveRequest;
import com.example.focustella.api.dto.request.FocusTagRequest;
import com.example.focustella.api.dto.response.DailySessionResponse;
import com.example.focustella.api.dto.response.FocusSessionCreateResponse;
import com.example.focustella.api.dto.response.FocusSessionResponse;
import com.example.focustella.api.dto.response.UserTagResponse;
import com.example.focustella.application.port.in.AddFocusTagUseCase;
import com.example.focustella.application.port.in.CreateFocusSessionUseCase;
import com.example.focustella.application.port.in.DeleteFocusTagUseCase;
import com.example.focustella.application.port.in.GetDailySessionUseCase;
import com.example.focustella.application.port.in.GetFocusSessionUseCase;
import com.example.focustella.application.port.in.GetFocusTagUseCase;
import com.example.focustella.application.port.in.SaveDailySessionUseCase;
import com.example.focustella.application.port.in.SaveFocusSessionUseCase;
import com.example.focustella.common.api.ApiResponse;
import com.example.focustella.domain.model.ChecklistItem;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/session")
public class SessionController implements SessionControllerDocs {

    private final SaveDailySessionUseCase saveDailySessionUseCase;
    private final GetDailySessionUseCase getDailySessionUseCase;
    private final CreateFocusSessionUseCase createFocusSessionUseCase;
    private final SaveFocusSessionUseCase saveFocusSessionUseCase;
    private final GetFocusSessionUseCase getFocusSessionUseCase;
    private final AddFocusTagUseCase addFocusTagUseCase;
    private final DeleteFocusTagUseCase deleteFocusTagUseCase;
    private final GetFocusTagUseCase getFocusTagUseCase;

    public SessionController(
            SaveDailySessionUseCase saveDailySessionUseCase,
            GetDailySessionUseCase getDailySessionUseCase,
            CreateFocusSessionUseCase createFocusSessionUseCase,
            SaveFocusSessionUseCase saveFocusSessionUseCase,
            GetFocusSessionUseCase getFocusSessionUseCase,
            AddFocusTagUseCase addFocusTagUseCase,
            DeleteFocusTagUseCase deleteFocusTagUseCase,
            GetFocusTagUseCase getFocusTagUseCase
    ) {
        this.saveDailySessionUseCase = saveDailySessionUseCase;
        this.getDailySessionUseCase = getDailySessionUseCase;
        this.createFocusSessionUseCase = createFocusSessionUseCase;
        this.saveFocusSessionUseCase = saveFocusSessionUseCase;
        this.getFocusSessionUseCase = getFocusSessionUseCase;
        this.addFocusTagUseCase = addFocusTagUseCase;
        this.deleteFocusTagUseCase = deleteFocusTagUseCase;
        this.getFocusTagUseCase = getFocusTagUseCase;
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

    @GetMapping("/focus/create")
    public ResponseEntity<ApiResponse<FocusSessionCreateResponse>> createFocusSession(
            Authentication authentication,
            @RequestParam("durationMinutes") Integer durationMinutes
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                createFocusSessionUseCase.prepare(authentication.getName(), durationMinutes)
        ));
    }

    @PostMapping("/focus")
    public ResponseEntity<ApiResponse<FocusSessionResponse>> saveFocusSession(
            Authentication authentication,
            @Valid @RequestBody FocusSessionSaveRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                saveFocusSessionUseCase.save(authentication.getName(), request)
        ));
    }

    @GetMapping("/focus")
    public ResponseEntity<ApiResponse<List<FocusSessionResponse>>> getFocusSessions(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                getFocusSessionUseCase.getCompletedSessions(authentication.getName())
        ));
    }

    @PostMapping("/focus/tag/add")
    public ResponseEntity<ApiResponse<UserTagResponse>> addFocusTag(
            Authentication authentication,
            @Valid @RequestBody FocusTagRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                UserTagResponse.from(addFocusTagUseCase.add(authentication.getName(), request.name()))
        ));
    }

    @DeleteMapping("/focus/tag/delete")
    public ResponseEntity<ApiResponse<UserTagResponse>> deleteFocusTag(
            Authentication authentication,
            @Valid @RequestBody FocusTagRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                UserTagResponse.from(deleteFocusTagUseCase.delete(authentication.getName(), request.name()))
        ));
    }

    @GetMapping("/focus/tag")
    public ResponseEntity<ApiResponse<List<UserTagResponse>>> getFocusTags(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                getFocusTagUseCase.getAll(authentication.getName()).stream()
                        .map(UserTagResponse::from)
                        .toList()
        ));
    }
}
