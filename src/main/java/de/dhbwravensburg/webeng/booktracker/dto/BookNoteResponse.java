package de.dhbwravensburg.webeng.booktracker.dto;

import java.time.LocalDateTime;

public record BookNoteResponse(
        Long id,
        Long shelfEntryId,
        Integer pageReference,
        String content,
        LocalDateTime createdAt
) {}