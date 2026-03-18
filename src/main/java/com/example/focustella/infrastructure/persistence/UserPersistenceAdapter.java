package com.example.focustella.infrastructure.persistence;

import com.example.focustella.application.port.out.LoadUserPort;
import com.example.focustella.application.port.out.SaveUserPort;
import com.example.focustella.domain.model.User;
import com.example.focustella.infrastructure.persistence.entity.UserEntity;
import com.example.focustella.infrastructure.persistence.repository.UserJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceAdapter implements SaveUserPort, LoadUserPort {

    private final UserJpaRepository userJpaRepository;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = userJpaRepository.save(new UserEntity(user.id(), user.seed(), user.type()));
        return toDomain(entity);
    }

    @Override
    public Optional<User> loadById(String id) {
        return userJpaRepository.findById(id)
                .map(this::toDomain);
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getSeed(),
                entity.getType(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
