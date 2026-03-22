package com.example.focustella.application.port.out;

import com.example.focustella.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface LoadUserByKeywordPort {
    List<User> loadByNicknameContaining(String nickname);
    Optional<User> loadByUserCode(String userCode);
}
