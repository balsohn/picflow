package com.example.picflow.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommentPageResponse {
    private List<CommentResponse> comments;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean first;
    private boolean last;

    public static CommentPageResponse from(Page<CommentResponse> page) {
        return new CommentPageResponse(
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
