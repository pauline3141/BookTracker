package de.dhbwravensburg.webeng.booktracker.mapper;

import de.dhbwravensburg.webeng.booktracker.dto.BookNoteRequest;
import de.dhbwravensburg.webeng.booktracker.dto.BookNoteResponse;
import de.dhbwravensburg.webeng.booktracker.model.BookNote;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;

import java.time.LocalDateTime;

public final class BookNoteMapper {

    private BookNoteMapper() {}

    public static BookNote toEntity(ShelfEntry shelfEntry, BookNoteRequest request) {
        BookNote note = new BookNote();
        note.setShelfEntry(shelfEntry);
        note.setPageReference(request.pageReference());
        note.setContent(request.content());
        note.setCreatedAt(LocalDateTime.now());
        return note;
    }

    public static BookNoteResponse toResponse(BookNote note) {
        return new BookNoteResponse(
                note.getId(),
                note.getShelfEntry().getId(),
                note.getPageReference(),
                note.getContent(),
                note.getCreatedAt()
        );
    }
}