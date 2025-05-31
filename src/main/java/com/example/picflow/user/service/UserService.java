package com.example.picflow.user.service;

import com.example.picflow.global.exception.InvalidPasswordException;
import com.example.picflow.global.exception.UserNotFoundException;
import com.example.picflow.global.util.PasswordEncoder;
import com.example.picflow.user.dto.PasswordChangeRequest;
import com.example.picflow.user.dto.UserPageResponse;
import com.example.picflow.user.dto.UserProfileUpdateRequest;
import com.example.picflow.user.dto.UserResponse;
import com.example.picflow.user.entity.User;
import com.example.picflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 본인 프로필 조회
    public UserResponse getMyProfile(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);
        return UserResponse.from(user);
    }

    // 다른 사용자 프로필 조회 (민감한 정보 제외)
    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);
        return UserResponse.fromPublic(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateProfile(request.getUsername(), request.getProfileImage(), request.getBio());
        return UserResponse.from(user);
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(UserNotFoundException::new);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 현재 비밀번호와 새 비밀번호가 같은지 확인
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidPasswordException("현재 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다.");
        }

        // 새 비밀번호 암호화 후 저장
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.changePassword(encodedNewPassword);
    }

    // 사용자명으로 검색
    public UserPageResponse searchByUsername(String username, Pageable pageable) {
        Page<User> users = userRepository.findByUsernameContainingAndDeletedFalse(username, pageable);
        Page<UserResponse> userResponses = users.map(UserResponse::fromPublic);
        return UserPageResponse.from(userResponses);
    }

    // 이메일로 검색
    public UserPageResponse searchByEmail(String email, Pageable pageable) {
        Page<User> users = userRepository.findByEmailContainingAndDeletedFalse(email, pageable);
        Page<UserResponse> userResponses = users.map(UserResponse::fromPublic);
        return UserPageResponse.from(userResponses);
    }

    // 모든 사용자 조회
    public UserPageResponse getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findByDeletedFalse(pageable);
        Page<UserResponse> userResponses = users.map(UserResponse::fromPublic);
        return UserPageResponse.from(userResponses);
    }
}
