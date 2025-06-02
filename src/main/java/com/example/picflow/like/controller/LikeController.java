package com.example.picflow.like.controller;

import com.example.picflow.global.common.dto.ApiResponse;
import com.example.picflow.like.dto.LikeResponse;
import com.example.picflow.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 게시물 좋아요
    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<ApiResponse<LikeResponse>> likePost(
            Authentication authentication,
            @PathVariable Long postId) {
        Long userId = (Long) authentication.getPrincipal();
        LikeResponse response = likeService.likePost(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("좋아요가 등록되었습니다.", response));
    }

    // 게시물 좋아요 취소
    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            Authentication authentication,
            @PathVariable Long postId) {
        Long userId = (Long) authentication.getPrincipal();
        likeService.unlikePost(userId, postId);
        return ResponseEntity.ok(ApiResponse.success("좋아요가 취소되었습니다."));
    }

    // 게시물의 좋아요 목록 조회
    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<ApiResponse<List<LikeResponse>>> getLikesByPost(@PathVariable Long postId) {
        List<LikeResponse> response = likeService.getLikesByPost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 게시물의 좋아요 수 조회
    @GetMapping("/posts/{postId}/likes/count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(@PathVariable Long postId) {
        long count = likeService.getLikeCount(postId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 사용자가 특정 게시물에 좋아요를 눌렀는지 확인
    @GetMapping("/posts/{postId}/likes/status")
    public ResponseEntity<ApiResponse<Boolean>> checkLikeStatus(
            Authentication authentication,
            @PathVariable Long postId) {
        Long userId = (Long) authentication.getPrincipal();
        boolean isLiked = likeService.isLikedByUser(userId, postId);
        return ResponseEntity.ok(ApiResponse.success(isLiked));
    }

    // 사용자가 좋아요한 게시물 목록 조회
    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<ApiResponse<List<LikeResponse>>> getLikesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<LikeResponse> response = likeService.getLikesByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 좋아요 삭제 (ID로)
    @DeleteMapping("/likes/{likeId}")
    public ResponseEntity<ApiResponse<Void>> deleteLike(
            Authentication authentication,
            @PathVariable Long likeId) {
        Long userId = (Long) authentication.getPrincipal();
        likeService.deleteLike(userId, likeId);
        return ResponseEntity.ok(ApiResponse.success("좋아요가 삭제되었습니다."));
    }
}