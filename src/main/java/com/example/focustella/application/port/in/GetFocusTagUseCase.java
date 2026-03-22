package com.example.focustella.application.port.in;

import com.example.focustella.domain.model.UserTag;
import java.util.List;

public interface GetFocusTagUseCase {
    List<UserTag> getAll(String userId);
}
