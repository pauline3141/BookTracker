package de.dhbwravensburg.webeng.booktracker.dto;

public record BookNoteRequest(
        Integer pageReference,
        String content,
        boolean isPublic
) {}
