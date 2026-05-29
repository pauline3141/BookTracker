package de.dhbwravensburg.webeng.booktracker.dto;

import de.dhbwravensburg.webeng.booktracker.model.ReadingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ShelfEntryRequest(

        @NotNull(message = "bookId must not be null")
        Long bookId,

        ReadingStatus status,

        @PositiveOrZero(message = "totalPages must be zero or positive")
        int totalPages
) {}