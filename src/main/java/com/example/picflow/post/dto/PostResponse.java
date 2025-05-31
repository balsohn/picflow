package com.example.picflow.post.dto;

import com.example.picflow.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long postId;
    private String title;
    private String content;
    private String imageUrl;
    private String author;
    private Long authorId;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getAuthor().getUsername(),
                post.getAuthor().getId(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
