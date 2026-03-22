package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface SearchUserUseCase {
    List<User> searchByNicknameOrCode(String keyword);
}