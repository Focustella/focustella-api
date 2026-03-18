package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.User;

public interface SaveUserPort {
    User save(User user);
}
