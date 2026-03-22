package com.example.focustella.api.controller;

import com.example.focustella.api.dto.response.AnonymousAuthResponse;
import com.example.focustella.api.dto.response.UserResponse;
import com.example.focustella.api.dto.response.UserSearchResponse;
import com.example.focustella.application.port.in.CreateAnonymousUserUseCase;
import com.example.focustella.application.port.in.SearchUserUseCase;
import com.example.focustella.common.api.ApiResponse;
import com.example.focustella.common.exception.BusinessException;
import com.example.focustella.common.exception.code.UserErrorCode;
import com.example.focustella.domain.model.User;
import com.example.focustella.infrastructure.config.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CreateAnonymousUserUseCase createAnonymousUserUseCase;
    private final SearchUserUseCase searchUserUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(
            CreateAnonymousUserUseCase createAnonymousUserUseCase,
            SearchUserUseCase searchUserUseCase,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.createAnonymousUserUseCase = createAnonymousUserUseCase;
        this.searchUserUseCase = searchUserUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/anonymous")
    public ResponseEntity<ApiResponse<AnonymousAuthResponse>> createAnonymous() {
        User user = createAnonymousUserUseCase.createAnonymous();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(new AnonymousAuthResponse(
                        jwtTokenProvider.createAccessToken(user),
                        UserResponse.from(user)
                )));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSearchResponse>>> searchUser(@RequestParam String keyword) {
        List<User> users = searchUserUseCase.searchByNicknameOrCode(keyword);
        
        if (users.isEmpty()) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }

        List<UserSearchResponse> responses = users.stream()
                .map(UserSearchResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
