package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.BookRequest;
import de.dhbwravensburg.webeng.booktracker.dto.BookResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.BookMapper;
import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Read and add books to the shared catalog")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all books", description = "Returns every book in the shared catalog")
    public List<BookResponse> getAll() {
        return service.findAll().stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public BookResponse getById(
            @Parameter(description = "ID of the book") @PathVariable Long id) {
        return BookMapper.toResponse(service.getOrThrow(id));
    }

    @PostMapping
    @Operation(summary = "Add a book to the catalog",
            description = "Creates a new book, or returns the existing one if a book with the same ISBN already exists")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created or existing book returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        Book created = service.create(BookMapper.toEntity(null, request));
        BookResponse response = BookMapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/books/" + created.getId()))
                .body(response);
    }
}