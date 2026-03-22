package com.example.focustella.application.service;

import com.example.focustella.application.port.in.CreateAnonymousUserUseCase;
import com.example.focustella.application.port.in.SearchUserUseCase;
import com.example.focustella.application.port.in.SignInUseCase;
import com.example.focustella.application.port.in.UpdateNicknameUseCase;
import com.example.focustella.application.port.out.LoadUserByEmailPort;
import com.example.focustella.application.port.out.LoadUserByKeywordPort;
import com.example.focustella.application.port.out.LoadUserPort;
import com.example.focustella.application.port.out.SaveUserPort;
import com.example.focustella.common.exception.BusinessException;
import com.example.focustella.common.exception.code.AuthErrorCode;
import com.example.focustella.common.exception.code.UserErrorCode;
import com.example.focustella.domain.model.User;
import com.example.focustella.domain.model.UserType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements CreateAnonymousUserUseCase, UpdateNicknameUseCase, SignInUseCase, SearchUserUseCase {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;
    private final LoadUserByEmailPort loadUserByEmailPort;
    private final LoadUserByKeywordPort loadUserByKeywordPort;

    public UserService(
            SaveUserPort saveUserPort,
            LoadUserPort loadUserPort,
            LoadUserByEmailPort loadUserByEmailPort,
            LoadUserByKeywordPort loadUserByKeywordPort
    ) {
        this.saveUserPort = saveUserPort;
        this.loadUserPort = loadUserPort;
        this.loadUserByEmailPort = loadUserByEmailPort;
        this.loadUserByKeywordPort = loadUserByKeywordPort;
    }

    @Override
    @Transactional
    public User createAnonymous() {
        return saveUserPort.save(new User(
                UUID.randomUUID().toString(),
                generateUserCode(), // 랜덤 8자리 문자열
                null, // 닉네임 초기값은 null
                null, // 이메일 초기값은 null
                ThreadLocalRandom.current().nextLong(),
                UserType.ANONYMOUS,
                null,
                null
        ));
    }

    @Override
    @Transactional
    public User updateNickname(String userId, String nickname) {
        if (nickname == null || nickname.trim().isEmpty() || nickname.length() > 10) {
            throw new BusinessException(UserErrorCode.INVALID_NICKNAME);
        }

        User user = loadUserPort.loadById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        User updatedUser = new User(
                user.id(),
                user.userCode(),
                nickname,
                user.email(),
                user.seed(),
                user.type(),
                user.createdAt(),
                user.updatedAt()
        );

        return saveUserPort.save(updatedUser);
    }

    @Override
    @Transactional
    public User signIn(String email) {
        Optional<User> existingUser = loadUserByEmailPort.loadByEmail(email);

        // 이미 가입된 유저라면 그대로 반환
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // 존재하지 않는 이메일이라면 자동으로 회원가입 처리
        User newUser = new User(
                UUID.randomUUID().toString(),
                generateUserCode(), // 랜덤 8자리 문자열 부여
                null,               // 닉네임은 추후 설정
                email,              // 전달받은 이메일 저장
                ThreadLocalRandom.current().nextLong(),
                UserType.MEMBER,    // 정식 멤버 타입으로 지정
                null,
                null
        );

        return saveUserPort.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchByNicknameOrCode(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        // 먼저 8자리 문자열(userCode)과 정확히 일치하는 유저 검색 시도
        Optional<User> userByCode = loadUserByKeywordPort.loadByUserCode(keyword);
        if (userByCode.isPresent()) {
            return List.of(userByCode.get());
        }

        // 코드가 없으면 닉네임의 "일부"라도 포함하는 모든 유저를 검색
        return loadUserByKeywordPort.loadByNicknameContaining(keyword);
    }

    // 8자리 랜덤 문자열 (알파벳 대문자 + 숫자) 생성 메서드
    private String generateUserCode() {
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = ThreadLocalRandom.current().nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}