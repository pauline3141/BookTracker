package de.dhbwravensburg.webeng.booktracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ShelfEntryRequest(

        @NotNull(message = "bookId must not be null")
        Long bookId,

        @PositiveOrZero(message = "totalPages must be zero or positive")
        int totalPages
) {}