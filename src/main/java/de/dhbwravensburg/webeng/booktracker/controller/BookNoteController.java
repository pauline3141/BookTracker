package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.BookNoteRequest;
import de.dhbwravensburg.webeng.booktracker.dto.BookNoteResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.BookNoteMapper;
import de.dhbwravensburg.webeng.booktracker.service.BookNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notes", description = "Create and manage personal notes on shelf entries")
public class BookNoteController {

    private final BookNoteService service;

    public BookNoteController(BookNoteService service) {
        this.service = service;
    }

    @GetMapping("/entries/{entryId}/notes")
    @Operation(summary = "List notes on an entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notes returned"),
            @ApiResponse(responseCode = "404", description = "Entry not found or not owned by the user")
    })
    public List<BookNoteResponse> getByEntry(
            @Parameter(description = "ID of the shelf entry") @PathVariable Long entryId) {
        return service.findByShelfEntryId(entryId).stream()
                .map(BookNoteMapper::toResponse)
                .toList();
    }

    @PostMapping("/entries/{entryId}/notes")
    @Operation(summary = "Add a note to an entry")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Note created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Entry not found or not owned by the user")
    })
    public ResponseEntity<BookNoteResponse> addNote(
            @Parameter(description = "ID of the shelf entry") @PathVariable Long entryId,
            @Valid @RequestBody BookNoteRequest request) {
        BookNoteResponse note = BookNoteMapper.toResponse(service.addNote(entryId, request));
        return ResponseEntity
                .created(URI.create("/api/notes/" + note.id()))
                .body(note);
    }

    @PutMapping("/notes/{noteId}")
    @Operation(summary = "Update a note")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Note updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Note not found or not owned by the user")
    })
    public BookNoteResponse updateNote(
            @Parameter(description = "ID of the note") @PathVariable Long noteId,
            @Valid @RequestBody BookNoteRequest request) {
        return BookNoteMapper.toResponse(service.updateNote(noteId, request));
    }

    @DeleteMapping("/notes/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a note")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Note deleted"),
            @ApiResponse(responseCode = "404", description = "Note not found or not owned by the user")
    })
    public void deleteNote(
            @Parameter(description = "ID of the note") @PathVariable Long noteId) {
        service.deleteNote(noteId);
    }
}