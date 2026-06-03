package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryRequest;
import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.ShelfEntryMapper;
import de.dhbwravensburg.webeng.booktracker.service.ShelfEntryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/shelves/{shelfId}/entries")
public class ShelfEntryController {

    private final ShelfEntryService service;

    public ShelfEntryController(ShelfEntryService service) {
        this.service = service;
    }

    public record ProgressUpdateRequest(int currentPage, int totalPages) {}
    public record MoveRequest(Long targetShelfId) {}

    @GetMapping
    public List<ShelfEntryResponse> getAll(@PathVariable Long shelfId) {
        return service.findByShelfId(shelfId).stream()
                .map(ShelfEntryMapper::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ShelfEntryResponse> addBook(
            @PathVariable Long shelfId,
            @Valid @RequestBody ShelfEntryRequest request) {
        ShelfEntryResponse entry = ShelfEntryMapper.toResponse(service.addBook(shelfId, request));
        return ResponseEntity
                .created(URI.create("/api/shelves/" + shelfId + "/entries/" + entry.id()))
                .body(entry);
    }

    @PatchMapping("/{entryId}/progress")
    public ShelfEntryResponse updateProgress(
            @PathVariable Long shelfId,
            @PathVariable Long entryId,
            @RequestBody ProgressUpdateRequest request) {
        return ShelfEntryMapper.toResponse(
                service.updateProgress(entryId, request.currentPage(), request.totalPages()));
    }

    @PatchMapping("/{entryId}/move")
    public ShelfEntryResponse moveToShelf(
            @PathVariable Long shelfId,
            @PathVariable Long entryId,
            @RequestBody MoveRequest request) {
        return ShelfEntryMapper.toResponse(
                service.moveToShelf(entryId, request.targetShelfId()));
    }

    @DeleteMapping("/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBook(
            @PathVariable Long shelfId,
            @PathVariable Long entryId) {
        service.removeBook(entryId);
    }
}