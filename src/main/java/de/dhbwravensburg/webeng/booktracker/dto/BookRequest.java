package de.dhbwravensburg.webeng.booktracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @Schema(description = "Title of the book", example = "The Hobbit")
        @NotBlank(message = "title must not be blank")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        @Schema(description = "Author of the book", example = "J.R.R. Tolkien")
        @NotBlank(message = "author must not be blank")
        @Size(max = 200, message = "author must be at most 200 characters")
        String author,

        @Schema(description = "ISBN identifier", example = "9780261103344")
        @Size(max = 20, message = "isbn must be at most 20 characters")
        String isbn,

        @Schema(description = "URL of the cover image")
        String coverUrl,

        @Schema(description = "Year the book was first published", example = "1937")
        @PositiveOrZero(message = "publishYear must be zero or positive")
        int publishYear,

        @Schema(description = "Total number of pages", example = "310")
        @PositiveOrZero(message = "totalPages must be zero or positive")
        int totalPages
) {}