package de.dhbwravensburg.webeng.booktracker.mapper;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfRequest;
import de.dhbwravensburg.webeng.booktracker.dto.ShelfResponse;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;

import java.time.LocalDateTime;

public final class ShelfMapper {

    private ShelfMapper() {}

    public static Shelf toEntity(Long id, ShelfRequest request) {
        return new Shelf(
                id,
                request.name(),
                request.description(),
                LocalDateTime.now(),
                null
        );
    }

    public static ShelfResponse toResponse(Shelf shelf) {
        return new ShelfResponse(
                shelf.getId(),
                shelf.getName(),
                shelf.getDescription(),
                shelf.getCreatedAt()
        );
    }
}