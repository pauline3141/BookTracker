package de.dhbwravensburg.webeng.booktracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record BookNoteResponse(
        @Schema(description = "Unique note ID", example = "1")
        Long id,

        @Schema(description = "ID of the shelf entry this note belongs to", example = "1")
        Long shelfEntryId,

        @Schema(description = "Optional page the note refers to", example = "42")
        Integer pageReference,

        @Schema(description = "Note text", example = "Great twist in this chapter")
        String content,

        @Schema(description = "When the note was created")
        LocalDateTime createdAt
) {}