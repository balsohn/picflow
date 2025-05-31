package com.example.picflow.global.exception;

public class DuplicateException extends RuntimeException {
    public DuplicateException() {
        super("이미 존재하는 데이터입니다.");
    }

    public DuplicateException(String message) {
        super(message);
    }
}