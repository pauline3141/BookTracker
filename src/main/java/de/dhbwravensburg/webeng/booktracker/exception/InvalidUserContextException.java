package de.dhbwravensburg.webeng.booktracker.exception;

public class InvalidUserContextException extends RuntimeException {

    public InvalidUserContextException(String username) {
        super("No user found for authenticated principal: " + username);
    }
}