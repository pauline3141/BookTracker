package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.dto.BookNoteRequest;
import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.mapper.BookNoteMapper;
import de.dhbwravensburg.webeng.booktracker.model.BookNote;
import de.dhbwravensburg.webeng.booktracker.repository.BookNoteRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookNoteService {

    private final BookNoteRepository noteRepository;
    private final ShelfEntryRepository shelfEntryRepository;

    public BookNoteService(BookNoteRepository noteRepository,
                           ShelfEntryRepository shelfEntryRepository) {
        this.noteRepository = noteRepository;
        this.shelfEntryRepository = shelfEntryRepository;
    }

    public List<BookNote> findByShelfEntryId(Long shelfEntryId) {
        return noteRepository.findByShelfEntryId(shelfEntryId);
    }

    public List<BookNote> findPublic() {
        return noteRepository.findByIsPublicTrue();
    }

    public BookNote addNote(Long shelfEntryId, BookNoteRequest request) {
        var shelfEntry = shelfEntryRepository.findById(shelfEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("ShelfEntry", shelfEntryId));
        return noteRepository.save(BookNoteMapper.toEntity(shelfEntry, request));
    }

    public BookNote updateNote(Long noteId, BookNoteRequest request) {
        BookNote existing = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("BookNote", noteId));
        existing.setPageReference(request.pageReference());
        existing.setContent(request.content());
        existing.setPublic(request.isPublic());
        return noteRepository.save(existing);
    }

    public void deleteNote(Long noteId) {
        if (!noteRepository.existsById(noteId)) {
            throw new ResourceNotFoundException("BookNote", noteId);
        }
        noteRepository.deleteById(noteId);
    }
}