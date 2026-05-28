package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryRequest;
import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.ShelfEntryMapper;
import de.dhbwravensburg.webeng.booktracker.model.ReadingStatus;
import de.dhbwravensburg.webeng.booktracker.service.ShelfEntryService;
import jakarta.validation.Valid;
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

    public record StatusUpdateRequest(ReadingStatus status) {}

    public record ProgressUpdateRequest(int currentPage, int totalPages) {}

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
        return service.addBook(shelfId, request)
                .map(ShelfEntryMapper::toResponse)
                .map(entry -> ResponseEntity
                        .created(URI.create("/api/shelves/" + shelfId + "/entries/" + entry.id()))
                        .body(entry))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{entryId}")
    public ResponseEntity<ShelfEntryResponse> updateStatus(
            @PathVariable Long shelfId,
            @PathVariable Long entryId,
            @RequestBody StatusUpdateRequest request) {
        return service.updateStatus(entryId, request.status())
                .map(ShelfEntryMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{entryId}/progress")
    public ResponseEntity<ShelfEntryResponse> updateProgress(
            @PathVariable Long shelfId,
            @PathVariable Long entryId,
            @RequestBody ProgressUpdateRequest request) {
        return service.updateProgress(entryId, request.currentPage(), request.totalPages())
                .map(ShelfEntryMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> removeBook(
            @PathVariable Long shelfId,
            @PathVariable Long entryId) {
        if (service.removeBook(entryId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}