package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.User;
import java.util.Optional;

public interface LoadUserPort {
    Optional<User> loadById(String id);
}
