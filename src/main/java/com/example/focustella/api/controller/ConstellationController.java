package com.example.focustella.api.controller;

import com.example.focustella.api.docs.ConstellationControllerDocs;
import com.example.focustella.api.dto.request.ConstellationCreateRequest;
import com.example.focustella.api.dto.response.ConstellationResponse;
import com.example.focustella.application.port.in.CreateConstellationUseCase;
import com.example.focustella.common.api.ApiResponse;
import com.example.focustella.domain.model.Constellation;
import com.example.focustella.domain.model.ConstellationEdgeDraft;
import com.example.focustella.domain.model.ConstellationStarDraft;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/constellation")
public class ConstellationController implements ConstellationControllerDocs {

    private final CreateConstellationUseCase createConstellationUseCase;

    public ConstellationController(CreateConstellationUseCase createConstellationUseCase) {
        this.createConstellationUseCase = createConstellationUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConstellationResponse>> create(@Valid @RequestBody ConstellationCreateRequest request) {
        Constellation constellation = createConstellationUseCase.create(
                request.name(),
                request.createdBy(),
                request.defaultScale(),
                request.minScale(),
                request.maxScale(),
                request.stars().stream()
                        .map(star -> new ConstellationStarDraft(star.vectorX(), star.vectorY()))
                        .toList(),
                request.edges() == null ? List.of() : request.edges().stream()
                        .map(edge -> new ConstellationEdgeDraft(edge.fromStarIndex(), edge.toStarIndex()))
                        .toList()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ConstellationResponse.from(constellation)));
    }
}
