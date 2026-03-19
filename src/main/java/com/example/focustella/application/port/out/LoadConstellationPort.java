package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.Constellation;
import java.util.List;
import java.util.Optional;

public interface LoadConstellationPort {
    List<Constellation> loadActiveByStarCountRange(int minStarCount, int maxStarCount);

    Optional<Constellation> loadById(Long id);
}
