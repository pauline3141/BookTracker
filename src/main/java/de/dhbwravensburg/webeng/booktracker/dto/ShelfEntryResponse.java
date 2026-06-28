package de.dhbwravensburg.webeng.booktracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ShelfEntryResponse(
        @Schema(description = "Unique entry ID", example = "1")
        Long id,

        @Schema(description = "ID of the shelf this entry belongs to", example = "1")
        Long shelfId,

        @Schema(description = "The book on this shelf entry")
        BookResponse book,

        @Schema(description = "When the book was added to the shelf")
        LocalDateTime addedAt,

        @Schema(description = "Page the reader has currently reached", example = "42")
        int currentPage,

        @Schema(description = "Total number of pages for this entry", example = "310")
        int totalPages
) {}