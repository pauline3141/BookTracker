package de.dhbwravensburg.webeng.booktracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ShelfEntryRequest(
        @Schema(description = "ID of the book to add to the shelf", example = "1")
        @NotNull(message = "bookId must not be null")
        Long bookId,

        @Schema(description = "Total number of pages for this entry", example = "310")
        @PositiveOrZero(message = "totalPages must be zero or positive")
        int totalPages
) {}