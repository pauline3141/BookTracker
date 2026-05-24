package de.dhbwravensburg.webeng.booktracker.dto;

import de.dhbwravensburg.webeng.booktracker.model.ReadingStatus;

public record ShelfEntryRequest(
        Long bookId,
        ReadingStatus status,
        int totalPages
) {}