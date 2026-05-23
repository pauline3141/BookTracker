package de.dhbwravensburg.webeng.booktracker.mapper;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryRequest;
import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryResponse;
import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.model.ReadingStatus;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;

import java.time.LocalDateTime;

public final class ShelfEntryMapper {

    private ShelfEntryMapper() {}

    public static ShelfEntry toEntity(Shelf shelf, Book book, ShelfEntryRequest request) {
        ShelfEntry entry = new ShelfEntry();
        entry.setShelf(shelf);
        entry.setBook(book);
        entry.setStatus(request.status() != null ? request.status() : ReadingStatus.WANT_TO_READ);
        entry.setAddedAt(LocalDateTime.now());
        return entry;
    }

    public static ShelfEntryResponse toResponse(ShelfEntry entry) {
        return new ShelfEntryResponse(
                entry.getId(),
                entry.getShelf().getId(),
                BookMapper.toResponse(entry.getBook()),
                entry.getStatus(),
                entry.getAddedAt()
        );
    }
}
