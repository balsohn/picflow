package com.example.picflow.post.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String title;

    private String content;
    private String imageUrl;
}
