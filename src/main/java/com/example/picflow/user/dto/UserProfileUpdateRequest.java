package com.example.picflow.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileUpdateRequest {

    @Size(min = 2, max = 10, message = "사용자명은 2자 이상 10자 이하여야 합니다.")
    private String username;

    private String profileImage;

    @Size(max = 500, message = "자기소개는 500자 이하여야 합니다.")
    private String bio;
}
