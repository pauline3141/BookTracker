package de.dhbwravensburg.webeng.booktracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShelfRequest(
        @Schema(description = "Name of the shelf", example = "Currently Reading")
        @NotBlank(message = "name must not be blank")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @Schema(description = "Optional description of the shelf", example = "Books I'm working through right now")
        @Size(max = 500, message = "description must be at most 500 characters")
        String description
) {}