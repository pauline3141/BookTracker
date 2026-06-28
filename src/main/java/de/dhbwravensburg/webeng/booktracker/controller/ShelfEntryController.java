package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryRequest;
import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.ShelfEntryMapper;
import de.dhbwravensburg.webeng.booktracker.service.ShelfEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/shelves/{shelfId}/entries")
@Tag(name = "Shelf Entries", description = "Add books to shelves, track progress, and move entries between shelves")
public class ShelfEntryController {

    private final ShelfEntryService service;

    public ShelfEntryController(ShelfEntryService service) {
        this.service = service;
    }

    public record ProgressUpdateRequest(int currentPage, int totalPages) {}
    public record MoveRequest(Long targetShelfId) {}

    @GetMapping
    @Operation(summary = "List entries on a shelf")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entries returned"),
            @ApiResponse(responseCode = "404", description = "Shelf not found or not owned by the user")
    })
    public List<ShelfEntryResponse> getAll(
            @Parameter(description = "ID of the shelf") @PathVariable Long shelfId) {
        return service.findByShelfId(shelfId).stream()
                .map(ShelfEntryMapper::toResponse)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Add a book to a shelf")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entry created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Shelf or book not found, or shelf not owned by the user")
    })
    public ResponseEntity<ShelfEntryResponse> addBook(
            @Parameter(description = "ID of the shelf") @PathVariable Long shelfId,
            @Valid @RequestBody ShelfEntryRequest request) {
        ShelfEntryResponse entry = ShelfEntryMapper.toResponse(service.addBook(shelfId, request));
        return ResponseEntity
                .created(URI.create("/api/shelves/" + shelfId + "/entries/" + entry.id()))
                .body(entry);
    }

    @PatchMapping("/{entryId}/progress")
    @Operation(summary = "Update reading progress for an entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress updated"),
            @ApiResponse(responseCode = "404", description = "Entry not found or not owned by the user")
    })
    public ShelfEntryResponse updateProgress(
            @Parameter(description = "ID of the shelf") @PathVariable Long shelfId,
            @Parameter(description = "ID of the entry") @PathVariable Long entryId,
            @RequestBody ProgressUpdateRequest request) {
        return ShelfEntryMapper.toResponse(
                service.updateProgress(entryId, request.currentPage(), request.totalPages()));
    }

    @PatchMapping("/{entryId}/move")
    @Operation(summary = "Move an entry to another shelf")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry moved"),
            @ApiResponse(responseCode = "404", description = "Entry or target shelf not found, or not owned by the user")
    })
    public ShelfEntryResponse moveToShelf(
            @Parameter(description = "ID of the shelf") @PathVariable Long shelfId,
            @Parameter(description = "ID of the entry") @PathVariable Long entryId,
            @RequestBody MoveRequest request) {
        return ShelfEntryMapper.toResponse(
                service.moveToShelf(entryId, request.targetShelfId()));
    }

    @DeleteMapping("/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove an entry from a shelf", description = "Cascades to all notes on the entry")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entry removed"),
            @ApiResponse(responseCode = "404", description = "Entry not found or not owned by the user")
    })
    public void removeBook(
            @Parameter(description = "ID of the shelf") @PathVariable Long shelfId,
            @Parameter(description = "ID of the entry") @PathVariable Long entryId) {
        service.removeBook(entryId);
    }
}