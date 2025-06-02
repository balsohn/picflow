package com.example.picflow.comment.controller;

import com.example.picflow.comment.dto.CommentCreateRequest;
import com.example.picflow.comment.dto.CommentPageResponse;
import com.example.picflow.comment.dto.CommentResponse;
import com.example.picflow.comment.dto.CommentUpdateRequest;
import com.example.picflow.comment.service.CommentService;
import com.example.picflow.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        CommentResponse response = commentService.createComment(userId, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글이 작성되었습니다.", response));
    }

    // 게시물의 댓글 목록 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentPageResponse>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        CommentPageResponse response = commentService.getCommentsByPost(postId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            Authentication authentication,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        CommentResponse response = commentService.updateComment(userId, commentId, request);
        return ResponseEntity.ok(ApiResponse.success("댓글이 수정되었습니다.", response));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            Authentication authentication,
            @PathVariable Long commentId) {
        Long userId = (Long) authentication.getPrincipal();
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다."));
    }

    // 특정 사용자의 댓글 목록 조회
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<ApiResponse<CommentPageResponse>> getCommentsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        CommentPageResponse response = commentService.getCommentsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 게시물의 댓글 수 조회
    @GetMapping("/posts/{postId}/comments/count")
    public ResponseEntity<ApiResponse<Long>> getCommentCount(@PathVariable Long postId) {
        long count = commentService.getCommentCount(postId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}