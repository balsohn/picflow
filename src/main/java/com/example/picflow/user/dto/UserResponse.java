package com.example.picflow.user.dto;

import com.example.picflow.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String profileImage;
    private String bio;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImage(),
                user.getBio(),
                user.getCreatedAt()
        );
    }

    // 민감한 정보 제외 응답
    public static UserResponse fromPublic(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                null,
                user.getProfileImage(),
                user.getBio(),
                user.getCreatedAt()
        );
    }
}
