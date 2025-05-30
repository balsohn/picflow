package com.example.picflow.like.entity;

import com.example.picflow.global.common.entity.BaseTimeEntity;
import com.example.picflow.post.entity.Post;
import com.example.picflow.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    // 정적 팩토리 메서드
    public static Like create(Post post, User user) {
        // 자신의 게시물에는 좋아요를 누를 수 없음
        if (post.getAuthor().getId().equals(user.getId())) {
            throw new IllegalArgumentException("자신의 게시물에는 좋아요를 누를 수 없습니다.");
        }
        return Like.builder()
                .post(post)
                .user(user)
                .build();
    }

    // 좋아요 관계 확인
    public boolean isLikeRelation(Post post, User user) {
        return this.post.getId().equals(post.getId())
                && this.user.getId().equals(user.getId());
    }
}