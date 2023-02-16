package com.example.moviemanager.service.tmdbmovieservice.exception;

public class UniqueMovieDetailsNotFoundException extends RuntimeException {

    public UniqueMovieDetailsNotFoundException(String message) {
        super(message);
    }
}
