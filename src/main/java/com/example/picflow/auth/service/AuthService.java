package com.example.picflow.auth.service;

import com.example.picflow.auth.dto.LoginRequest;
import com.example.picflow.auth.dto.LoginResponse;
import com.example.picflow.auth.dto.SignupRequest;
import com.example.picflow.auth.dto.WithdrawRequest;
import com.example.picflow.global.util.JwtUtil;
import com.example.picflow.global.util.PasswordEncoder;
import com.example.picflow.user.entity.User;
import com.example.picflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signup(SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
        User user = User.create(request.getEmail(), encodedPassword, request.getUsername());
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmailAndDeleteFalse(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getId());

        return new LoginResponse(token, user.getId(), user.getUsername());
    }

    @Transactional
    public void withdraw(Long userId, WithdrawRequest request) {
        // 사용자 조회
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 회원 탈퇴 처리
        user.withdraw();
    }
}