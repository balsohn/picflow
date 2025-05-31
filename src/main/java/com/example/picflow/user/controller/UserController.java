package com.example.picflow.user.controller;

import com.example.picflow.global.common.dto.ApiResponse;
import com.example.picflow.user.dto.PasswordChangeRequest;
import com.example.picflow.user.dto.UserPageResponse;
import com.example.picflow.user.dto.UserProfileUpdateRequest;
import com.example.picflow.user.dto.UserResponse;
import com.example.picflow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 본인 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 다른 사용자 프로필 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(@PathVariable Long userId) {
        UserResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileUpdateRequest request
            ) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다.", response));
    }

    // 비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordChangeRequest request
            ) {
        Long userId = (Long) authentication.getPrincipal();
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 변경되었습니다."));
    }

    // 사용자명으로 검색
    @GetMapping("/search/username")
    public ResponseEntity<ApiResponse<UserPageResponse>> searchByUsername(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        UserPageResponse response = userService.searchByUsername(username, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 이메일로 검색
    @GetMapping("/search/email")
    public ResponseEntity<ApiResponse<UserPageResponse>> searchByEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        UserPageResponse response = userService.searchByEmail(email, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 모든 사용자 조회
    public ResponseEntity<ApiResponse<UserPageResponse>>  getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        UserPageResponse response = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
