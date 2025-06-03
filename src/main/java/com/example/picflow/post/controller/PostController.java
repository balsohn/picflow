package com.example.picflow.post.controller;

import com.example.picflow.file.service.FileService;
import com.example.picflow.file.dto.FileUploadResponse;
import com.example.picflow.global.common.dto.ApiResponse;
import com.example.picflow.post.dto.PostCreateRequest;
import com.example.picflow.post.dto.PostPageResponse;
import com.example.picflow.post.dto.PostResponse;
import com.example.picflow.post.dto.PostUpdateRequest;
import com.example.picflow.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FileService fileService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            Authentication authentication,
            @Valid @RequestPart("post") PostCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        Long userId = (Long) authentication.getPrincipal();

        // 이미지 파일이 있으면 업로드 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            FileUploadResponse fileUploadResponse = fileService.storeFile(imageFile);
            request.setImageUrl(fileUploadResponse.getFileViewUri());
        }

        PostResponse response = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시물이 작성되었습니다.", response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PostPageResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        PostPageResponse response = postService.getAllPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/newsfeed")
    public ResponseEntity<ApiResponse<PostPageResponse>> getNewsFeed(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = (Long) authentication.getPrincipal();
        PageRequest pageable = PageRequest.of(page, size);
        PostPageResponse response = postService.getNewsFeed(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PostPageResponse>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        PostPageResponse response = postService.getUserPosts(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestPart("post") PostUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        Long userId = (Long) authentication.getPrincipal();

        // 새 이미지 파일이 있으면 업로드 처리
        if (imageFile != null && !imageFile.isEmpty()) {
            FileUploadResponse fileUploadResponse = fileService.storeFile(imageFile);
            request.setImageUrl(fileUploadResponse.getFileViewUri());
        }

        PostResponse response = postService.updatePost(userId, postId, request);
        return ResponseEntity.ok(ApiResponse.success("게시물이 수정되었습니다.", response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            Authentication authentication,
            @PathVariable Long postId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        postService.deletePost(userId, postId);
        return ResponseEntity.ok(ApiResponse.success("게시물이 삭제되었습니다."));
    }

    // 검색 기능들은 기존과 동일하게 유지
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<PostPageResponse>> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PostPageResponse response = postService.searchByTitle(title, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/content")
    public ResponseEntity<ApiResponse<PostPageResponse>> searchByContent(
            @RequestParam String content,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PostPageResponse response = postService.searchByContent(content, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/date")
    public ResponseEntity<ApiResponse<PostPageResponse>> searchByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PostPageResponse response = postService.searchByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}