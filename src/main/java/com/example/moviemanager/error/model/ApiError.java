package com.example.moviemanager.error.model;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiError {

    private HttpStatus httpStatus;
    private String message;
    private LocalDateTime localDateTime;

    public ApiError(HttpStatus httpStatus, String message, LocalDateTime localDateTime) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.localDateTime = localDateTime;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
