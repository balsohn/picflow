package com.example.picflow.comment.entity;

import com.example.picflow.global.common.entity.BaseTimeEntity;
import com.example.picflow.post.entity.Post;
import com.example.picflow.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 500, message = "댓글은 500자 이하여야 합니다.")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Builder
    public Comment(String content, Post post, User author) {
        this.content = content;
        this.post = post;
        this.author = author;
    }

    // 정적 팩토리 메서드
    public static Comment create(String content, Post post, User author) {
        return Comment.builder()
                .content(content)
                .post(post)
                .author(author)
                .build();
    }

    // 댓글 수정
    public void update(String content) {
        if (content != null && !content.trim().isEmpty()) {
            this.content = content;
        }
    }

    // 작성자 확인
    public boolean isAuthor(User user) {
        return this.author.getId().equals(user.getId());
    }
}