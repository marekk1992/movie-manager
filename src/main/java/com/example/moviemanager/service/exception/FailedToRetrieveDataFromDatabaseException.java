package com.example.moviemanager.service.exception;

public class FailedToRetrieveDataFromDatabaseException extends RuntimeException {

    public FailedToRetrieveDataFromDatabaseException(String message) {
        super(message);
    }
}
