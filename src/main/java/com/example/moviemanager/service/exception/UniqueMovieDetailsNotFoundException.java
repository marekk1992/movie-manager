package com.example.moviemanager.service.exception;

public class UniqueMovieDetailsNotFoundException extends RuntimeException {

    public UniqueMovieDetailsNotFoundException(String message) {
        super(message);
    }
}
