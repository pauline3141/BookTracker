package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.BookResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.BookMapper;
import de.dhbwravensburg.webeng.booktracker.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<BookResponse> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(BookMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}