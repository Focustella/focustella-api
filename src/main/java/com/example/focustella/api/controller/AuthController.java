package com.example.focustella.api.controller;

import com.example.focustella.api.dto.response.AnonymousAuthResponse;
import com.example.focustella.api.dto.response.UserResponse;
import com.example.focustella.application.port.in.CreateAnonymousUserUseCase;
import com.example.focustella.common.api.ApiResponse;
import com.example.focustella.domain.model.User;
import com.example.focustella.infrastructure.config.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final CreateAnonymousUserUseCase createAnonymousUserUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(
            CreateAnonymousUserUseCase createAnonymousUserUseCase,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.createAnonymousUserUseCase = createAnonymousUserUseCase;
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
}
