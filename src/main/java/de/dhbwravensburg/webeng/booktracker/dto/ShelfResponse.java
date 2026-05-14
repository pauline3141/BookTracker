package de.dhbwravensburg.webeng.booktracker.dto;

import java.time.LocalDateTime;

public record ShelfResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt
) {}
