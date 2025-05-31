package com.example.picflow.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostPageResponse {
    private List<PostResponse> posts;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean first;
    private boolean last;

    public static PostPageResponse from(Page<PostResponse> page) {
        return new PostPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.isFirst(),
                page.isLast()
        );
    }
}