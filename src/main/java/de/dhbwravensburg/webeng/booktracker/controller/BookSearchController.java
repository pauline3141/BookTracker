package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.BookResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.OpenLibraryMapper;
import de.dhbwravensburg.webeng.booktracker.service.OpenLibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Search", description = "Public endpoints backed by the Open Library API (no authentication required)")
public class BookSearchController {

    private final OpenLibraryService openLibraryService;

    public BookSearchController(OpenLibraryService openLibraryService) {
        this.openLibraryService = openLibraryService;
    }

    @GetMapping("/search")
    @Operation(summary = "Search the Open Library catalog", description = "Searches books by title, author, or ISBN with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned"),
            @ApiResponse(responseCode = "400", description = "Missing required query parameter"),
            @ApiResponse(responseCode = "502", description = "Open Library API unavailable")
    })
    public List<BookResponse> search(
            @Parameter(description = "Search term (title, author, or ISBN)") @RequestParam String q,
            @Parameter(description = "Result offset for pagination") @RequestParam(defaultValue = "0") int offset) {
        return openLibraryService.search(q, offset).stream()
                .map(OpenLibraryMapper::toResponse)
                .toList();
    }

    @GetMapping("/discover")
    @Operation(summary = "Discover popular books", description = "Returns popular books from a randomly chosen subject via the Open Library Subjects API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Discover results returned"),
            @ApiResponse(responseCode = "502", description = "Open Library API unavailable")
    })
    public List<BookResponse> discover() {
        return openLibraryService.discover().stream()
                .map(OpenLibraryMapper::toResponse)
                .toList();
    }
}