package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.Constellation;
import com.example.focustella.domain.model.ConstellationDraft;

public interface SaveConstellationPort {

    Constellation save(ConstellationDraft draft);

    boolean existsActiveByName(String name);
}
