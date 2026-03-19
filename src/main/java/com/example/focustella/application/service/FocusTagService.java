package com.example.focustella.application.service;

import com.example.focustella.application.port.in.AddFocusTagUseCase;
import com.example.focustella.application.port.in.DeleteFocusTagUseCase;
import com.example.focustella.application.port.in.GetFocusTagUseCase;
import com.example.focustella.application.port.out.DeleteUserTagPort;
import com.example.focustella.application.port.out.LoadUserPort;
import com.example.focustella.application.port.out.LoadUserTagPort;
import com.example.focustella.application.port.out.SaveUserTagPort;
import com.example.focustella.common.exception.BusinessException;
import com.example.focustella.common.exception.code.CommonErrorCode;
import com.example.focustella.common.exception.code.UserTagErrorCode;
import com.example.focustella.domain.model.UserTag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FocusTagService implements AddFocusTagUseCase, DeleteFocusTagUseCase, GetFocusTagUseCase {

    private final LoadUserPort loadUserPort;
    private final LoadUserTagPort loadUserTagPort;
    private final SaveUserTagPort saveUserTagPort;
    private final DeleteUserTagPort deleteUserTagPort;

    public FocusTagService(
            LoadUserPort loadUserPort,
            LoadUserTagPort loadUserTagPort,
            SaveUserTagPort saveUserTagPort,
            DeleteUserTagPort deleteUserTagPort
    ) {
        this.loadUserPort = loadUserPort;
        this.loadUserTagPort = loadUserTagPort;
        this.saveUserTagPort = saveUserTagPort;
        this.deleteUserTagPort = deleteUserTagPort;
    }

    @Override
    @Transactional
    public UserTag add(String userId, String tagName) {
        loadUserPort.loadById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String normalizedName = normalize(tagName);
        return loadUserTagPort.loadByUserIdAndNormalizedName(userId, normalizedName)
                .orElseGet(() -> saveUserTagPort.save(new UserTag(
                        null,
                        userId,
                        tagName.trim(),
                        normalizedName,
                        null,
                        null
                )));
    }

    @Override
    @Transactional
    public UserTag delete(String userId, String tagName) {
        String normalizedName = normalize(tagName);
        UserTag userTag = loadUserTagPort.loadByUserIdAndNormalizedName(userId, normalizedName)
                .orElseThrow(() -> new BusinessException(UserTagErrorCode.TAG_NOT_FOUND));
        deleteUserTagPort.delete(userTag);
        return userTag;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<UserTag> getAll(String userId) {
        return loadUserTagPort.loadByUserId(userId);
    }

    private String normalize(String tagName) {
        if (tagName == null) {
            throw new BusinessException(UserTagErrorCode.INVALID_NAME);
        }

        String normalized = tagName.trim().replaceAll("\\s+", " ").toLowerCase();
        if (normalized.isBlank()) {
            throw new BusinessException(UserTagErrorCode.INVALID_NAME);
        }
        return normalized;
    }
}
