package com.example.focustella.infrastructure.persistence;

import com.example.focustella.application.port.out.DeleteUserTagPort;
import com.example.focustella.application.port.out.LoadUserTagPort;
import com.example.focustella.application.port.out.SaveUserTagPort;
import com.example.focustella.domain.model.UserTag;
import com.example.focustella.infrastructure.persistence.entity.UserTagEntity;
import com.example.focustella.infrastructure.persistence.repository.UserTagJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserTagPersistenceAdapter implements LoadUserTagPort, SaveUserTagPort, DeleteUserTagPort {

    private final UserTagJpaRepository userTagJpaRepository;

    public UserTagPersistenceAdapter(UserTagJpaRepository userTagJpaRepository) {
        this.userTagJpaRepository = userTagJpaRepository;
    }

    @Override
    public Optional<UserTag> loadByUserIdAndNormalizedName(String userId, String normalizedName) {
        return userTagJpaRepository.findByUserIdAndNormalizedName(userId, normalizedName)
                .map(this::toDomain);
    }

    @Override
    public java.util.List<UserTag> loadByUserId(String userId) {
        return userTagJpaRepository.findByUserIdOrderByNameAsc(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public UserTag save(UserTag userTag) {
        UserTagEntity saved = userTagJpaRepository.save(new UserTagEntity(
                userTag.userId(),
                userTag.name(),
                userTag.normalizedName()
        ));
        return toDomain(saved);
    }

    @Override
    public void delete(UserTag userTag) {
        userTagJpaRepository.deleteById(userTag.id());
    }

    private UserTag toDomain(UserTagEntity entity) {
        return new UserTag(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getNormalizedName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
