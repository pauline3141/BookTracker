package de.dhbwravensburg.webeng.booktracker.exception;

public class ExternalApiClientException extends RuntimeException {

    public ExternalApiClientException(String message) {
        super(message);
    }
}