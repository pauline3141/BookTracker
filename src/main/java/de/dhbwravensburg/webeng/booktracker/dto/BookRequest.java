package de.dhbwravensburg.webeng.booktracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BookRequest(

        @NotBlank(message = "title must not be blank")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        @NotBlank(message = "author must not be blank")
        @Size(max = 200, message = "author must be at most 200 characters")
        String author,

        @Size(max = 20, message = "isbn must be at most 20 characters")
        String isbn,

        String coverUrl,

        @Positive(message = "publishYear must be positive")
        int publishYear
) {}