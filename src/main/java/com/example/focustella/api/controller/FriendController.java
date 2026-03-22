package com.example.focustella.api.controller;

import com.example.focustella.api.dto.request.FriendAcceptRequest;
import com.example.focustella.api.dto.request.FriendRequest;
import com.example.focustella.api.dto.response.FriendResponse;
import com.example.focustella.application.port.in.FriendUseCase;
import com.example.focustella.application.port.out.LoadUserPort;
import com.example.focustella.common.api.ApiResponse;
import com.example.focustella.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {

    private final FriendUseCase friendUseCase;
    private final LoadUserPort loadUserPort; // 친구의 정보를 조회하기 위해 주입

    public FriendController(FriendUseCase friendUseCase, LoadUserPort loadUserPort) {
        this.friendUseCase = friendUseCase;
        this.loadUserPort = loadUserPort;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FriendResponse>>> getFriends(@RequestParam String userId) {
        List<FriendResponse> responses = friendUseCase.getFriends(userId).stream()
                .map(relation -> {
                    // 나와 친구인 상대방의 ID 찾기 (내가 요청자면 receiverId가 친구, 내가 수신자면 requesterId가 친구)
                    String friendId = relation.getRequesterId().equals(userId) ? relation.getReceiverId() : relation.getRequesterId();
                    
                    // 친구의 User 정보를 조회
                    Optional<User> friendInfo = loadUserPort.loadById(friendId);
                    
                    String nickname = friendInfo.map(User::nickname).orElse(null);
                    String userCode = friendInfo.map(User::userCode).orElse(null);

                    return FriendResponse.from(relation, nickname, userCode);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<FriendResponse>> requestFriend(@RequestBody FriendRequest request) {
        var relation = friendUseCase.requestFriend(request.requesterId(), request.receiverId());
        
        // 요청 시에도 상대방(receiver)의 정보를 채워서 응답
        Optional<User> receiverInfo = loadUserPort.loadById(request.receiverId());
        String nickname = receiverInfo.map(User::nickname).orElse(null);
        String userCode = receiverInfo.map(User::userCode).orElse(null);

        FriendResponse response = FriendResponse.from(relation, nickname, userCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<Void>> acceptFriendRequest(@RequestBody FriendAcceptRequest request) {
        if (request.accept()) {
            friendUseCase.acceptFriendRequest(request.relationId());
        } else {
            friendUseCase.rejectFriendRequest(request.relationId());
        }
        return ResponseEntity.ok(ApiResponse.emptySuccess());
    }
}
