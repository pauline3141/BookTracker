package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.BookNoteRequest;
import de.dhbwravensburg.webeng.booktracker.dto.BookNoteResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.BookNoteMapper;
import de.dhbwravensburg.webeng.booktracker.service.BookNoteService;
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
            @RequestBody BookNoteRequest request) {
        return service.addNote(entryId, request)
                .map(BookNoteMapper::toResponse)
                .map(note -> ResponseEntity
                        .created(URI.create("/api/notes/" + note.id()))
                        .body(note))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/notes/{noteId}")
    public ResponseEntity<BookNoteResponse> updateNote(
            @PathVariable Long noteId,
            @RequestBody BookNoteRequest request) {
        return service.updateNote(noteId, request)
                .map(BookNoteMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long noteId) {
        if (service.deleteNote(noteId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
