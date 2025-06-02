package com.example.picflow.like.dto;

import com.example.picflow.like.entity.Like;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {
    private Long likeId;
    private Long postId;
    private String postTitle;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;

    public static LikeResponse from(Like like) {
        return new LikeResponse(
                like.getId(),
                like.getPost().getId(),
                like.getPost().getTitle(),
                like.getUser().getId(),
                like.getUser().getUsername(),
                like.getCreatedAt()
        );
    }
}
