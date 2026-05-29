package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfRequest;
import de.dhbwravensburg.webeng.booktracker.dto.ShelfResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.ShelfMapper;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.service.ShelfService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ShelfResponse getById(@PathVariable Long id) {
        return ShelfMapper.toResponse(service.getOrThrow(id));
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
    public ShelfResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ShelfRequest request) {
        Shelf updated = service.update(id, ShelfMapper.toEntity(id, request));
        return ShelfMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}