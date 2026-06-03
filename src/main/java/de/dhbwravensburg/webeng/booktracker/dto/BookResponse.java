package de.dhbwravensburg.webeng.booktracker.dto;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        String coverUrl,
        int publishYear,
        int totalPages
) {}