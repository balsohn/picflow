package com.example.picflow.global.exception;

public class FollowNotFoundException extends RuntimeException {
    public FollowNotFoundException() {
        super("팔로우 관계가 존재하지 않습니다.");
    }

    public FollowNotFoundException(String message) {
        super(message);
    }
}