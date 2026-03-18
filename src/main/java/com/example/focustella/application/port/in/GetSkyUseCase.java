package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.Sky;

public interface GetSkyUseCase {
    Sky getSky(String id);
}
