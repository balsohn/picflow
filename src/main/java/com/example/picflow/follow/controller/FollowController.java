package com.example.picflow.follow.controller;

import com.example.picflow.follow.dto.FollowPageResponse;
import com.example.picflow.follow.service.FollowService;
import com.example.picflow.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // 팔로우
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            Authentication authentication,
            @PathVariable Long userId
    ) {
        Long followerId = (Long) authentication.getPrincipal();
        followService.follow(followerId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("팔로우가 완료되었습니다."));
    }

    // 언팔로우
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            Authentication authentication,
            @PathVariable Long userId
    ) {
        Long followerId = (Long) authentication.getPrincipal();
        followService.unfollow(followerId, userId);
        return ResponseEntity.ok(ApiResponse.success("언팔로우가 완료되었습니다."));
    }

    // 팔로잉 목록조회 (내가 팔로우하는 사람들)
    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<FollowPageResponse>> getFollowers(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = (Long) authentication.getPrincipal();
        PageRequest pageable = PageRequest.of(page, size);
        FollowPageResponse response = followService.getFollowers(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 특정 사용자의 팔로잉 목록 조회
    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<FollowPageResponse>> getUserFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        FollowPageResponse response = followService.getFollowing(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 특정 사용자의 팔로워 목록 조회
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<FollowPageResponse>> getUserFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        FollowPageResponse response = followService.getFollowers(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 팔로잉 수 조회
    @GetMapping("/following/count")
    public ResponseEntity<ApiResponse<Long>> getFollowingCount(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        long count = followService.getFollowingCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 팔로워 수 조회
    @GetMapping("/followers/count")
    public ResponseEntity<ApiResponse<Long>> getFollowersCount(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        long count = followService.getFollowersCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 팔로우 관계 확인
    @GetMapping("/status/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> checkFollowStatus(
            Authentication authentication,
            @PathVariable Long userId
    ) {
        Long followerId = (Long) authentication.getPrincipal();
        boolean isFollowing = followService.isFollowing(followerId, userId);
        return ResponseEntity.ok(ApiResponse.success(isFollowing));
    }
}
