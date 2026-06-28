package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.dto.BookNoteRequest;
import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.mapper.BookNoteMapper;
import de.dhbwravensburg.webeng.booktracker.model.BookNote;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.repository.BookNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookNoteService {

    private final BookNoteRepository noteRepository;
    private final ShelfEntryService shelfEntryService;

    public BookNoteService(BookNoteRepository noteRepository,
                           ShelfEntryService shelfEntryService) {
        this.noteRepository = noteRepository;
        this.shelfEntryService = shelfEntryService;
    }

    public List<BookNote> findByShelfEntryId(Long shelfEntryId) {
        shelfEntryService.getOwnedEntryOrThrow(shelfEntryId);
        return noteRepository.findByShelfEntryId(shelfEntryId);
    }

    public BookNote addNote(Long shelfEntryId, BookNoteRequest request) {
        ShelfEntry shelfEntry = shelfEntryService.getOwnedEntryOrThrow(shelfEntryId);
        return noteRepository.save(BookNoteMapper.toEntity(shelfEntry, request));
    }

    public BookNote updateNote(Long noteId, BookNoteRequest request) {
        BookNote existing = getOwnedNoteOrThrow(noteId);
        existing.setPageReference(request.pageReference());
        existing.setContent(request.content());
        return noteRepository.save(existing);
    }

    public void deleteNote(Long noteId) {
        getOwnedNoteOrThrow(noteId);
        noteRepository.deleteById(noteId);
    }

    private BookNote getOwnedNoteOrThrow(Long noteId) {
        BookNote note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("BookNote", noteId));
        shelfEntryService.getOwnedEntryOrThrow(note.getShelfEntry().getId());
        return note;
    }
}