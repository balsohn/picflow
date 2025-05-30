package com.example.picflow.user.entity;

import com.example.picflow.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 8자 이상이며, 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 2, max = 10, message = "사용자명은 2자 이상 10자 이하여야 합니다.")
    private String username;

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(nullable = false)
    private boolean deleted = false;

    @Builder
    public User(String email, String password, String username, String profileImage, String bio) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.profileImage = profileImage;
        this.bio = bio;
    }

    // 정적 팩토리 메서드
    public static User create(String email, String password, String username) {
        return User.builder()
                .email(email)
                .password(password)
                .username(username)
                .build();
    }

    // 프로필 업데이트
    public void updateProfile(String username, String profileImage, String bio) {
        if (username != null && !username.trim().isEmpty()) {
            this.username = username;
        }
        this.profileImage = profileImage;
        this.bio = bio;
    }

    // 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }

    // 회원 탈퇴
    public void withdraw() {
        this.deleted = true;
    }

}
