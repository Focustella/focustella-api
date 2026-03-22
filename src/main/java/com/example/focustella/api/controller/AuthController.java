package com.example.focustella.api.controller;

import com.example.focustella.api.dto.request.SignInRequest;
import com.example.focustella.api.dto.request.UpdateNicknameRequest;
import com.example.focustella.api.dto.response.AnonymousAuthResponse;
import com.example.focustella.api.dto.response.SignInResponse;
import com.example.focustella.api.dto.response.UserResponse;
import com.example.focustella.application.port.in.CreateAnonymousUserUseCase;
import com.example.focustella.application.port.in.SignInUseCase;
import com.example.focustella.application.port.in.UpdateNicknameUseCase;
import com.example.focustella.common.api.ApiResponse;
import com.example.focustella.domain.model.User;
import com.example.focustella.infrastructure.config.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final CreateAnonymousUserUseCase createAnonymousUserUseCase;
    private final SignInUseCase signInUseCase;
    private final UpdateNicknameUseCase updateNicknameUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(
            CreateAnonymousUserUseCase createAnonymousUserUseCase,
            SignInUseCase signInUseCase,
            UpdateNicknameUseCase updateNicknameUseCase,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.createAnonymousUserUseCase = createAnonymousUserUseCase;
        this.signInUseCase = signInUseCase;
        this.updateNicknameUseCase = updateNicknameUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping({"/anonymous"})
    public ResponseEntity<ApiResponse<AnonymousAuthResponse>> anonymousLogin() {
        User user = createAnonymousUserUseCase.createAnonymous();
        String accessToken = jwtTokenProvider.createAccessToken(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(new AnonymousAuthResponse(
                        accessToken,
                        UserResponse.from(user)
                )));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponse>> signIn(@RequestBody SignInRequest request) {
        User user = signInUseCase.signIn(request.email());
        String accessToken = jwtTokenProvider.createAccessToken(user);

        return ResponseEntity.ok(ApiResponse.success(new SignInResponse(
                accessToken,
                UserResponse.from(user)
        )));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<UserResponse>> updateNickname(
            @RequestBody UpdateNicknameRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        User updatedUser = updateNicknameUseCase.updateNickname(userId, request.nickname());
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(updatedUser)));
    }
}