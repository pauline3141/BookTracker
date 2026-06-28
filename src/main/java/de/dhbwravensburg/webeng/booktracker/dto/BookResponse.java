package de.dhbwravensburg.webeng.booktracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record BookResponse(
        @Schema(description = "Unique book ID", example = "1")
        Long id,

        @Schema(description = "Title of the book", example = "The Hobbit")
        String title,

        @Schema(description = "Author of the book", example = "J.R.R. Tolkien")
        String author,

        @Schema(description = "ISBN identifier", example = "9780261103344")
        String isbn,

        @Schema(description = "URL of the cover image")
        String coverUrl,

        @Schema(description = "Year the book was first published", example = "1937")
        int publishYear,

        @Schema(description = "Total number of pages", example = "310")
        int totalPages
) {}