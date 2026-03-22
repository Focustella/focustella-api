package com.example.focustella.application.service;

import com.example.focustella.application.port.in.CreateConstellationUseCase;
import com.example.focustella.application.port.out.SaveConstellationPort;
import com.example.focustella.common.exception.BusinessException;
import com.example.focustella.common.exception.code.ConstellationErrorCode;
import com.example.focustella.domain.model.Constellation;
import com.example.focustella.domain.model.ConstellationDraft;
import com.example.focustella.domain.model.ConstellationEdgeDraft;
import com.example.focustella.domain.model.ConstellationStarDraft;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConstellationService implements CreateConstellationUseCase {

    private final SaveConstellationPort saveConstellationPort;

    public ConstellationService(SaveConstellationPort saveConstellationPort) {
        this.saveConstellationPort = saveConstellationPort;
    }

    @Override
    @Transactional
    public Constellation create(
            String name,
            Long createdBy,
            Double defaultScale,
            Double minScale,
            Double maxScale,
            List<ConstellationStarDraft> stars,
            List<ConstellationEdgeDraft> edges
    ) {
        String normalizedName = name.trim();

        validateDuplicateName(normalizedName);
        validateScaleRange(defaultScale, minScale, maxScale);
        validateEdges(stars.size(), edges);

        return saveConstellationPort.save(new ConstellationDraft(
                normalizedName,
                createdBy,
                stars.size(),
                defaultScale == null ? 1.0 : defaultScale,
                minScale,
                maxScale,
                stars,
                edges
        ));
    }

    private void validateDuplicateName(String name) {
        if (saveConstellationPort.existsActiveByName(name)) {
            throw new BusinessException(ConstellationErrorCode.DUPLICATE_NAME);
        }
    }

    private void validateScaleRange(Double defaultScale, Double minScale, Double maxScale) {
        if (minScale != null && maxScale != null && minScale > maxScale) {
            throw new BusinessException(ConstellationErrorCode.INVALID_SCALE_RANGE, "minScale must be less than or equal to maxScale.");
        }
        if (defaultScale != null && minScale != null && defaultScale < minScale) {
            throw new BusinessException(ConstellationErrorCode.INVALID_SCALE_RANGE, "defaultScale must be greater than or equal to minScale.");
        }
        if (defaultScale != null && maxScale != null && defaultScale > maxScale) {
            throw new BusinessException(ConstellationErrorCode.INVALID_SCALE_RANGE, "defaultScale must be less than or equal to maxScale.");
        }
    }

    private void validateEdges(int starCount, List<ConstellationEdgeDraft> edges) {
        for (ConstellationEdgeDraft edge : edges) {
            if (edge.fromStarIndex() < 0 || edge.fromStarIndex() >= starCount
                    || edge.toStarIndex() < 0 || edge.toStarIndex() >= starCount) {
                throw new BusinessException(ConstellationErrorCode.INVALID_EDGE_INDEX, "Edge index is out of range.");
            }
            if (edge.fromStarIndex().equals(edge.toStarIndex())) {
                throw new BusinessException(ConstellationErrorCode.INVALID_EDGE_CONNECTION, "Edge must connect two different stars.");
            }
        }
    }
}
