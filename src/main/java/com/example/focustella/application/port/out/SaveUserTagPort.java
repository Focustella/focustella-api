package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.UserTag;

public interface SaveUserTagPort {
    UserTag save(UserTag userTag);
}
