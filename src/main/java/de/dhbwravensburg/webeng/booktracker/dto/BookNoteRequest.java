package de.dhbwravensburg.webeng.booktracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record BookNoteRequest(
        @Schema(description = "Optional page the note refers to", example = "42")
        @PositiveOrZero(message = "pageReference must be zero or positive")
        Integer pageReference,

        @Schema(description = "Note text", example = "Great twist in this chapter")
        @NotBlank(message = "content must not be blank")
        @Size(max = 2000, message = "content must be at most 2000 characters")
        String content
) {}