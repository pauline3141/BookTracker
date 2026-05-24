package de.dhbwravensburg.webeng.booktracker.dto;

import de.dhbwravensburg.webeng.booktracker.model.ReadingStatus;

import java.time.LocalDateTime;

public record ShelfEntryResponse(
        Long id,
        Long shelfId,
        BookResponse book,
        ReadingStatus status,
        LocalDateTime addedAt,
        int currentPage,
        int totalPages
) {}