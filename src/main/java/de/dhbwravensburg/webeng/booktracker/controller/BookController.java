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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Manage the shared book catalog")
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
    @Operation(summary = "Create a new book")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        Book created = service.create(BookMapper.toEntity(null, request));
        BookResponse response = BookMapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/books/" + created.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public BookResponse update(
            @Parameter(description = "ID of the book") @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        Book updated = service.update(id, BookMapper.toEntity(id, request));
        return BookMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book", description = "Cascades to all shelf entries and notes referencing the book")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public void delete(
            @Parameter(description = "ID of the book") @PathVariable Long id) {
        service.delete(id);
    }
}