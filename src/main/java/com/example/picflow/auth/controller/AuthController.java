package com.example.picflow.auth.controller;

import com.example.picflow.auth.dto.LoginRequest;
import com.example.picflow.auth.dto.LoginResponse;
import com.example.picflow.auth.dto.SignupRequest;
import com.example.picflow.auth.dto.WithdrawRequest;
import com.example.picflow.auth.service.AuthService;
import com.example.picflow.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", response));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            Authentication authentication,
            @Valid @RequestBody WithdrawRequest request
            ) {
        Long userId = (Long) authentication.getPrincipal();
        authService.withdraw(userId, request);

        return ResponseEntity.ok(ApiResponse.success("회원탈퇴가 완료되었습니다."));
    }
}
