package de.dhbwravensburg.webeng.booktracker.dto;

import java.time.LocalDateTime;

public record ShelfEntryResponse(
        Long id,
        Long shelfId,
        BookResponse book,
        LocalDateTime addedAt,
        int currentPage,
        int totalPages
) {}