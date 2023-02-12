package com.example.moviemanager.service.exception;

public class UniqueMovieNotFoundException extends RuntimeException {

    public UniqueMovieNotFoundException(String message) {
        super(message);
    }
}
