package com.example.focustella.application.service;

import com.example.focustella.application.port.in.CreateAnonymousUserUseCase;
import com.example.focustella.application.port.out.SaveUserPort;
import com.example.focustella.domain.model.User;
import com.example.focustella.domain.model.UserType;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements CreateAnonymousUserUseCase {

    private final SaveUserPort saveUserPort;

    public UserService(SaveUserPort saveUserPort) {
        this.saveUserPort = saveUserPort;
    }

    @Override
    @Transactional
    public User createAnonymous() {
        return saveUserPort.save(new User(
                UUID.randomUUID().toString(),
                ThreadLocalRandom.current().nextLong(),
                UserType.ANONYMOUS,
                null,
                null
        ));
    }
}
