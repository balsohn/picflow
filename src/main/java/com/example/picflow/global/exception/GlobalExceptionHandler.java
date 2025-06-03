package com.example.picflow.global.exception;

import com.example.picflow.global.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 정적 리소스 관련 예외 (404 처리)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(
            NoResourceFoundException e, HttpServletRequest request) {

        String requestURI = request.getRequestURI();

        // API 경로가 아닌 경우 (정적 리소스 요청)
        if (!requestURI.startsWith("/api/")) {
            log.warn("정적 리소스를 찾을 수 없음: {}", requestURI);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("요청한 리소스를 찾을 수 없습니다."));
        }

        // API 경로인 경우
        log.error("API 엔드포인트를 찾을 수 없음: {}", requestURI);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("API 엔드포인트를 찾을 수 없습니다."));
    }

    // 사용자 관련 예외
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException e) {
        log.error("UserNotFoundException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }

    // 게시물 관련 예외
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePostNotFound(PostNotFoundException e) {
        log.error("PostNotFoundException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }

    // 댓글 관련 예외
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCommentNotFound(CommentNotFoundException e) {
        log.error("CommentNotFoundException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }

    // 팔로우 관련 예외
    @ExceptionHandler(FollowNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleFollowNotFound(FollowNotFoundException e) {
        log.error("FollowNotFoundException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }

    // 권한 관련 예외
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException e) {
        log.error("UnauthorizedException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
    }

    // 중복 데이터 예외
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateException e) {
        log.error("DuplicateException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(e.getMessage()));
    }

    // 비밀번호 불일치 예외
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPassword(InvalidPasswordException e) {
        log.error("InvalidPasswordException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }

    // 일반적인 잘못된 인자 예외
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
    }

    // 유효성 검증 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "입력값이 올바르지 않습니다.";

        log.error("MethodArgumentNotValidException: {}", message);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(message));
    }

    // 예상치 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        // API 요청인 경우만 에러 로그
        if (requestURI.startsWith("/api/")) {
            log.error("Unexpected API error on {}: ", requestURI, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 내부 오류가 발생했습니다."));
        }

        // 정적 리소스 요청은 간단히 처리
        log.warn("Static resource error: {}", requestURI);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("요청한 리소스를 찾을 수 없습니다."));
    }
}