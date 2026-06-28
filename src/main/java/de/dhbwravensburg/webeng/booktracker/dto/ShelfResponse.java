package de.dhbwravensburg.webeng.booktracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ShelfResponse(
        @Schema(description = "Unique shelf ID", example = "1")
        Long id,

        @Schema(description = "Name of the shelf", example = "Currently Reading")
        String name,

        @Schema(description = "Optional description of the shelf")
        String description,

        @Schema(description = "When the shelf was created")
        LocalDateTime createdAt
) {}