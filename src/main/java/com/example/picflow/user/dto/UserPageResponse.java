package com.example.picflow.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserPageResponse {
    private List<UserResponse> users;
    private int currentPage;
    private int totalPage;
    private long totalElements;
    private int pageSize;
    private boolean first;
    private boolean last;

    public static UserPageResponse from(Page<UserResponse> page) {
        return new UserPageResponse(
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
