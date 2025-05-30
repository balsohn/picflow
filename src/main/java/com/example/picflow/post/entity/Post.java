package com.example.picflow.post.entity;

import com.example.picflow.global.common.entity.BaseTimeEntity;
import com.example.picflow.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @Builder
    public Post(String title, String content, String imageUrl, User author) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.author = author;
    }

    // 정적 팩토리 메서드
    public static Post create(String title, String content, String imageUrl, User author) {
        return Post.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .author(author)
                .build();
    }

    // 게시물 수정
    public void update(String title, String content, String imageUrl) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
        if (content != null && !content.trim().isEmpty()) {
            this.content = content;
        }

        this.imageUrl = imageUrl;
    }

    // 좋아요 수 증가
    public void increaseLikeCount() {
        this.likeCount++;
    }

    // 좋아요 수 감소
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    // 작성자 확인
    public boolean isAuthor(User user) {
        return this.author.getId().equals(user.getId());
    }
}
