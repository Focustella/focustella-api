package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.User;
import java.util.Optional;

public interface LoadUserByEmailPort {
    Optional<User> loadByEmail(String email);
}
