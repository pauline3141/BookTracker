package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.BookNoteRequest;
import de.dhbwravensburg.webeng.booktracker.dto.BookNoteResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.BookNoteMapper;
import de.dhbwravensburg.webeng.booktracker.service.BookNoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookNoteController {

    private final BookNoteService service;

    public BookNoteController(BookNoteService service) {
        this.service = service;
    }

    @GetMapping("/entries/{entryId}/notes")
    public List<BookNoteResponse> getByEntry(@PathVariable Long entryId) {
        return service.findByShelfEntryId(entryId).stream()
                .map(BookNoteMapper::toResponse)
                .toList();
    }

    @GetMapping("/notes/public")
    public List<BookNoteResponse> getPublic() {
        return service.findPublic().stream()
                .map(BookNoteMapper::toResponse)
                .toList();
    }

    @PostMapping("/entries/{entryId}/notes")
    public ResponseEntity<BookNoteResponse> addNote(
            @PathVariable Long entryId,
            @Valid @RequestBody BookNoteRequest request) {
        BookNoteResponse note = BookNoteMapper.toResponse(service.addNote(entryId, request));
        return ResponseEntity
                .created(URI.create("/api/notes/" + note.id()))
                .body(note);
    }

    @PutMapping("/notes/{noteId}")
    public BookNoteResponse updateNote(
            @PathVariable Long noteId,
            @Valid @RequestBody BookNoteRequest request) {
        return BookNoteMapper.toResponse(service.updateNote(noteId, request));
    }

    @DeleteMapping("/notes/{noteId}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable Long noteId) {
        service.deleteNote(noteId);
    }
}