package com.example.focustella.infrastructure.persistence;

import com.example.focustella.application.port.out.LoadFocusSessionPort;
import com.example.focustella.application.port.out.SaveFocusSessionPort;
import com.example.focustella.domain.model.FocusSession;
import com.example.focustella.domain.model.FocusSessionStatus;
import com.example.focustella.infrastructure.persistence.entity.ConstellationEntity;
import com.example.focustella.infrastructure.persistence.entity.FocusSessionEntity;
import com.example.focustella.infrastructure.persistence.repository.ConstellationJpaRepository;
import com.example.focustella.infrastructure.persistence.repository.FocusSessionJpaRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class FocusSessionPersistenceAdapter implements SaveFocusSessionPort, LoadFocusSessionPort {

    private final FocusSessionJpaRepository focusSessionJpaRepository;
    private final ConstellationJpaRepository constellationJpaRepository;

    public FocusSessionPersistenceAdapter(
            FocusSessionJpaRepository focusSessionJpaRepository,
            ConstellationJpaRepository constellationJpaRepository
    ) {
        this.focusSessionJpaRepository = focusSessionJpaRepository;
        this.constellationJpaRepository = constellationJpaRepository;
    }

    @Override
    public Set<Long> loadUsedConstellationIdsByUserId(String userId) {
        return new HashSet<>(focusSessionJpaRepository.findDistinctConstellationIdsByUserId(userId));
    }

    @Override
    public Optional<FocusSession> loadByIdAndUserId(String sessionId, String userId) {
        return focusSessionJpaRepository.findByIdAndUserId(sessionId, userId)
                .map(this::toDomain);
    }

    @Override
    public List<FocusSession> loadByUserId(String userId) {
        return focusSessionJpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public FocusSession save(FocusSession focusSession) {
        FocusSessionEntity saved = focusSessionJpaRepository.findById(focusSession.id())
                .map(existing -> updateExisting(existing, focusSession))
                .orElseGet(() -> createNew(focusSession));

        return toDomain(saved);
    }

    private FocusSessionEntity createNew(FocusSession focusSession) {
        ConstellationEntity constellation = constellationJpaRepository.getReferenceById(focusSession.constellationId());
        return focusSessionJpaRepository.save(new FocusSessionEntity(
                focusSession.id(),
                focusSession.userId(),
                constellation,
                focusSession.durationMinutes(),
                focusSession.status()
        ));
    }

    private FocusSessionEntity updateExisting(FocusSessionEntity existing, FocusSession focusSession) {
        existing.complete(
                focusSession.startedAt(),
                focusSession.endedAt(),
                focusSession.slotSeconds(),
                focusSession.discoveredStarCount(),
                focusSession.topicTags(),
                focusSession.rating(),
                focusSession.freeText()
        );
        return focusSessionJpaRepository.save(existing);
    }

    private FocusSession toDomain(FocusSessionEntity saved) {
        return new FocusSession(
                saved.getId(),
                saved.getUserId(),
                saved.getConstellation().getId(),
                saved.getDurationMinutes(),
                saved.getStartedAt(),
                saved.getEndedAt(),
                saved.getSlotSeconds(),
                saved.getDiscoveredStarCount(),
                List.copyOf(saved.getTopicTags()),
                saved.getRating(),
                saved.getFreeText(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }
}
