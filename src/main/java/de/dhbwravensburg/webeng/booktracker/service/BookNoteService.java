package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.dto.BookNoteRequest;
import de.dhbwravensburg.webeng.booktracker.mapper.BookNoteMapper;
import de.dhbwravensburg.webeng.booktracker.model.BookNote;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.repository.BookNoteRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<BookNote> addNote(Long shelfEntryId, BookNoteRequest request) {
        Optional<ShelfEntry> shelfEntry = shelfEntryRepository.findById(shelfEntryId);
        if (shelfEntry.isEmpty()) {
            return Optional.empty();
        }
        BookNote note = BookNoteMapper.toEntity(shelfEntry.get(), request);
        return Optional.of(noteRepository.save(note));
    }

    public Optional<BookNote> updateNote(Long noteId, BookNoteRequest request) {
        return noteRepository.findById(noteId).map(existing -> {
            existing.setPageReference(request.pageReference());
            existing.setContent(request.content());
            existing.setPublic(request.isPublic());
            return noteRepository.save(existing);
        });
    }

    public boolean deleteNote(Long noteId) {
        if (!noteRepository.existsById(noteId)) {
            return false;
        }
        noteRepository.deleteById(noteId);
        return true;
    }
}
