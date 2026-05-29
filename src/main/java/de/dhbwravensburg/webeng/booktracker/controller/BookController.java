package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.BookRequest;
import de.dhbwravensburg.webeng.booktracker.dto.BookResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.BookMapper;
import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookResponse> getAll() {
        return service.findAll().stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id) {
        return BookMapper.toResponse(service.getOrThrow(id));
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        Book created = service.create(BookMapper.toEntity(null, request));
        BookResponse response = BookMapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/books/" + created.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    public BookResponse update(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        Book updated = service.update(id, BookMapper.toEntity(id, request));
        return BookMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}