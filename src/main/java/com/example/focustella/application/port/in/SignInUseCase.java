package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.User;

public interface SignInUseCase {
    User signIn(String email);
}