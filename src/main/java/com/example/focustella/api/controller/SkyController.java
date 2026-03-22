package com.example.focustella.api.controller;

import com.example.focustella.api.docs.SkyControllerDocs;
import com.example.focustella.api.dto.response.SkyResponse;
import com.example.focustella.application.port.in.GetSkyUseCase;
import com.example.focustella.common.api.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sky")
public class SkyController implements SkyControllerDocs {

    private final GetSkyUseCase getSkyUseCase;

    public SkyController(GetSkyUseCase getSkyUseCase) {
        this.getSkyUseCase = getSkyUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkyResponse>> getSky(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(SkyResponse.from(getSkyUseCase.getSky(id))));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SkyResponse>> getMySky(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                SkyResponse.from(getSkyUseCase.getSky(authentication.getName()))
        ));
    }
}
