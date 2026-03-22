package com.example.focustella.infrastructure.persistence;

import com.example.focustella.application.port.out.LoadConstellationPort;
import com.example.focustella.application.port.out.SaveConstellationPort;
import com.example.focustella.domain.model.Constellation;
import com.example.focustella.domain.model.ConstellationDraft;
import com.example.focustella.domain.model.ConstellationEdge;
import com.example.focustella.domain.model.ConstellationStar;
import com.example.focustella.infrastructure.persistence.entity.ConstellationEdgeEntity;
import com.example.focustella.infrastructure.persistence.entity.ConstellationEntity;
import com.example.focustella.infrastructure.persistence.entity.ConstellationStarEntity;
import com.example.focustella.infrastructure.persistence.repository.ConstellationJpaRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConstellationPersistenceAdapter implements SaveConstellationPort, LoadConstellationPort {

    private final ConstellationJpaRepository constellationJpaRepository;

    public ConstellationPersistenceAdapter(ConstellationJpaRepository constellationJpaRepository) {
        this.constellationJpaRepository = constellationJpaRepository;
    }

    @Override
    public Constellation save(ConstellationDraft draft) {
        ConstellationEntity constellation = new ConstellationEntity(
                draft.name(),
                draft.createdBy(),
                draft.starCount(),
                draft.defaultScale(),
                draft.minScale(),
                draft.maxScale()
        );

        List<ConstellationStarEntity> starEntities = draft.stars().stream()
                .map(star -> new ConstellationStarEntity(star.vectorX(), star.vectorY()))
                .toList();
        constellation.setStars(starEntities);

        List<ConstellationStarEntity> persistedStars = new ArrayList<>(constellation.getStars());
        for (var edgeDraft : draft.edges()) {
            constellation.addEdge(new ConstellationEdgeEntity(
                    persistedStars.get(edgeDraft.fromStarIndex()),
                    persistedStars.get(edgeDraft.toStarIndex())
            ));
        }

        ConstellationEntity saved = constellationJpaRepository.save(constellation);

        return new Constellation(
                saved.getId(),
                saved.getName(),
                saved.getCreatedBy(),
                saved.getStarCount(),
                saved.getDefaultScale(),
                saved.getMinScale(),
                saved.getMaxScale(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getStars().stream()
                        .map(star -> new ConstellationStar(star.getId(), star.getVectorX(), star.getVectorY()))
                        .toList(),
                saved.getEdges().stream()
                        .map(edge -> new ConstellationEdge(
                                edge.getId(),
                                edge.getFromStar().getId(),
                                edge.getToStar().getId()
                        ))
                        .toList()
        );
    }

    @Override
    public boolean existsActiveByName(String name) {
        return constellationJpaRepository.existsByNameAndDeletedAtIsNull(name);
    }

    @Override
    public List<Constellation> loadActiveByStarCountRange(int minStarCount, int maxStarCount) {
        return constellationJpaRepository.findByDeletedAtIsNullAndStarCountBetween(minStarCount, maxStarCount)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public java.util.Optional<Constellation> loadById(Long id) {
        return constellationJpaRepository.findByIdAndDeletedAtIsNull(id)
                .map(this::toDomain);
    }

    private Constellation toDomain(ConstellationEntity saved) {
        return new Constellation(
                saved.getId(),
                saved.getName(),
                saved.getCreatedBy(),
                saved.getStarCount(),
                saved.getDefaultScale(),
                saved.getMinScale(),
                saved.getMaxScale(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                saved.getStars().stream()
                        .map(star -> new ConstellationStar(star.getId(), star.getVectorX(), star.getVectorY()))
                        .toList(),
                saved.getEdges().stream()
                        .map(edge -> new ConstellationEdge(
                                edge.getId(),
                                edge.getFromStar().getId(),
                                edge.getToStar().getId()
                        ))
                        .toList()
        );
    }
}
