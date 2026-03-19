package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.UserTag;
import java.util.List;
import java.util.Optional;

public interface LoadUserTagPort {
    Optional<UserTag> loadByUserIdAndNormalizedName(String userId, String normalizedName);

    List<UserTag> loadByUserId(String userId);
}
