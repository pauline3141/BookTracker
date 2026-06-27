package de.dhbwravensburg.webeng.booktracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record BookNoteRequest(

        @PositiveOrZero(message = "pageReference must be zero or positive")
        Integer pageReference,

        @NotBlank(message = "content must not be blank")
        @Size(max = 2000, message = "content must be at most 2000 characters")
        String content
) {}