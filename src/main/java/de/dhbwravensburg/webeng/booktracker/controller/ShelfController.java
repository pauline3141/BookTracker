package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfRequest;
import de.dhbwravensburg.webeng.booktracker.dto.ShelfResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.ShelfMapper;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.service.ShelfService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/shelves")
public class ShelfController {

    private final ShelfService service;

    public ShelfController(ShelfService service) {
        this.service = service;
    }

    @GetMapping
    public List<ShelfResponse> getAll() {
        return service.findAll().stream()
                .map(ShelfMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShelfResponse> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ShelfMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ShelfResponse> create(@Valid @RequestBody ShelfRequest request) {
        Shelf created = service.create(ShelfMapper.toEntity(null, request));
        ShelfResponse response = ShelfMapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/shelves/" + created.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShelfResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ShelfRequest request) {
        return service.update(id, ShelfMapper.toEntity(id, request))
                .map(ShelfMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}