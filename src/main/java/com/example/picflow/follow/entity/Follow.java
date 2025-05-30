package com.example.picflow.follow.entity;

import com.example.picflow.global.common.entity.BaseTimeEntity;
import com.example.picflow.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id, following_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @Builder
    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
    }

    // 정정 팩토리 메서드
    public static Follow create(User follower, User following) {
        if (follower.getId().equals(following.getId())) {
            throw new IllegalArgumentException("자기 자신을 팔로우 할 수 없습니다.");
        }
        return Follow.builder()
                .follower(follower)
                .following(following)
                .build();
    }

    // 팔로우 관계 확인
    public boolean isFollowRelation(User follower, User following) {
        return this.follower.getId().equals(follower.getId())
                && this.following.getId().equals(following.getId());
    }
}
