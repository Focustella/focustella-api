package com.example.focustella.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "constellation")
public class ConstellationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "star_count", nullable = false)
    private Integer starCount;

    @Column(name = "default_scale", nullable = false)
    private Double defaultScale;

    @Column(name = "min_scale")
    private Double minScale;

    @Column(name = "max_scale")
    private Double maxScale;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "constellation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ConstellationStarEntity> stars = new ArrayList<>();

    @OneToMany(mappedBy = "constellation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ConstellationEdgeEntity> edges = new ArrayList<>();

    public ConstellationEntity(
            String name,
            Long createdBy,
            Integer starCount,
            Double defaultScale,
            Double minScale,
            Double maxScale
    ) {
        this.name = name;
        this.createdBy = createdBy;
        this.starCount = starCount;
        this.defaultScale = defaultScale;
        this.minScale = minScale;
        this.maxScale = maxScale;
    }

    public void addStar(ConstellationStarEntity star) {
        stars.add(star);
        star.setConstellation(this);
    }

    public void setStars(List<ConstellationStarEntity> stars) {
        this.stars.clear();
        if (stars != null) {
            stars.forEach(this::addStar);
        }
    }

    public void addEdge(ConstellationEdgeEntity edge) {
        edges.add(edge);
        edge.setConstellation(this);
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
