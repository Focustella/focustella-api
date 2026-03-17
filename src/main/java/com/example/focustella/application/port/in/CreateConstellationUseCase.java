package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.Constellation;
import com.example.focustella.domain.model.ConstellationEdgeDraft;
import com.example.focustella.domain.model.ConstellationStarDraft;
import java.util.List;

public interface CreateConstellationUseCase {

    Constellation create(
            String name,
            Long createdBy,
            Double defaultScale,
            Double minScale,
            Double maxScale,
            List<ConstellationStarDraft> stars,
            List<ConstellationEdgeDraft> edges
    );
}
