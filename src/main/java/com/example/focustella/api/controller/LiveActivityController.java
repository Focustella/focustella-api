package com.example.focustella.api.controller;

import com.example.focustella.api.dto.request.LiveActivityEndRequest;
import com.example.focustella.api.dto.request.LiveActivityPauseRequest;
import com.example.focustella.api.dto.request.LiveActivityRegisterRequest;
import com.example.focustella.api.dto.request.LiveActivityResumeRequest;
import com.example.focustella.api.dto.response.LiveActivityRegisterResponse;
import com.example.focustella.api.dto.response.LiveActivityStatusResponse;
import com.example.focustella.application.service.LiveActivityService;
import com.example.focustella.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/live-activity/focus")
public class LiveActivityController {

    private final LiveActivityService liveActivityService;

    public LiveActivityController(LiveActivityService liveActivityService) {
        this.liveActivityService = liveActivityService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LiveActivityRegisterResponse>> register(
            Authentication authentication,
            @Valid @RequestBody LiveActivityRegisterRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                liveActivityService.register(authentication.getName(), request)
        ));
    }

    @PostMapping("/pause")
    public ResponseEntity<ApiResponse<LiveActivityStatusResponse>> pause(
            Authentication authentication,
            @Valid @RequestBody LiveActivityPauseRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                liveActivityService.pause(authentication.getName(), request)
        ));
    }

    @PostMapping("/resume")
    public ResponseEntity<ApiResponse<LiveActivityStatusResponse>> resume(
            Authentication authentication,
            @Valid @RequestBody LiveActivityResumeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                liveActivityService.resume(authentication.getName(), request)
        ));
    }

    @PostMapping("/end")
    public ResponseEntity<ApiResponse<LiveActivityStatusResponse>> end(
            Authentication authentication,
            @Valid @RequestBody LiveActivityEndRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                liveActivityService.end(authentication.getName(), request)
        ));
    }
}
