package com.example.picflow.follow.dto;

import com.example.picflow.follow.entity.Follow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class FollowPageResponse {
    private List<FollowResponse> follows;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean first;
    private boolean last;

    public static FollowPageResponse from(Page<FollowResponse> page) {
        return new FollowPageResponse(
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
