package com.example.focustella.infrastructure.persistence;

import com.example.focustella.application.port.out.LoadUserByEmailPort;
import com.example.focustella.application.port.out.LoadUserByKeywordPort;
import com.example.focustella.application.port.out.LoadUserPort;
import com.example.focustella.application.port.out.SaveUserPort;
import com.example.focustella.domain.model.User;
import com.example.focustella.infrastructure.persistence.entity.UserEntity;
import com.example.focustella.infrastructure.persistence.repository.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceAdapter implements SaveUserPort, LoadUserPort, LoadUserByEmailPort, LoadUserByKeywordPort {

    private final UserJpaRepository userJpaRepository;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity;
        if (user.createdAt() != null) {
            entity = new UserEntity(
                    user.id(),
                    user.userCode(),
                    user.nickname(),
                    user.email(),
                    user.seed(),
                    user.type(),
                    user.createdAt(),
                    user.updatedAt()
            );
        } else {
            entity = new UserEntity(
                    user.id(),
                    user.userCode(),
                    user.nickname(),
                    user.email(),
                    user.seed(),
                    user.type()
            );
        }

        UserEntity savedEntity = userJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<User> loadById(String id) {
        return userJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> loadByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public List<User> loadByNicknameContaining(String nickname) {
        return userJpaRepository.findByNicknameContainingIgnoreCase(nickname).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> loadByUserCode(String userCode) {
        return userJpaRepository.findByUserCode(userCode)
                .map(this::toDomain);
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUserCode(),
                entity.getNickname(),
                entity.getEmail(),
                entity.getSeed(),
                entity.getType(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
