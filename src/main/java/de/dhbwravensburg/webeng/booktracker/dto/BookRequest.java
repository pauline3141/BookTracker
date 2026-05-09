package de.dhbwravensburg.webeng.booktracker.dto;

public record BookRequest(
        String title,
        String author,
        String isbn,
        String coverUrl,
        int publishYear
) {}
