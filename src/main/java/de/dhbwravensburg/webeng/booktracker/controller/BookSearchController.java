package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.BookResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.OpenLibraryMapper;
import de.dhbwravensburg.webeng.booktracker.service.OpenLibraryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookSearchController {

    private final OpenLibraryService openLibraryService;

    public BookSearchController(OpenLibraryService openLibraryService) {
        this.openLibraryService = openLibraryService;
    }

    @GetMapping("/search")
    public List<BookResponse> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int offset) {
        return openLibraryService.search(q, offset).stream()
                .map(OpenLibraryMapper::toResponse)
                .toList();
    }
}