package com.example.picflow.follow.dto;

import com.example.picflow.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponse {
    private Long userId;
    private String username;
    private String profileImage;
    private LocalDateTime followAt;

    public static FollowResponse from(User user, LocalDateTime followAt) {
        return new FollowResponse(
                user.getId(),
                user.getUsername(),
                user.getProfileImage(),
                followAt
        );
    }
}
