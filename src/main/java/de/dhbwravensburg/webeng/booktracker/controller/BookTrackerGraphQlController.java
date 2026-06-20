package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.BookResponse;
import de.dhbwravensburg.webeng.booktracker.dto.ShelfRequest;
import de.dhbwravensburg.webeng.booktracker.mapper.OpenLibraryMapper;
import de.dhbwravensburg.webeng.booktracker.mapper.ShelfMapper;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.service.OpenLibraryService;
import de.dhbwravensburg.webeng.booktracker.service.ShelfEntryService;
import de.dhbwravensburg.webeng.booktracker.service.ShelfService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BookTrackerGraphQlController {

    private final ShelfService shelfService;
    private final ShelfEntryService shelfEntryService;
    private final OpenLibraryService openLibraryService;

    public BookTrackerGraphQlController(ShelfService shelfService,
                                        ShelfEntryService shelfEntryService,
                                        OpenLibraryService openLibraryService) {
        this.shelfService = shelfService;
        this.shelfEntryService = shelfEntryService;
        this.openLibraryService = openLibraryService;
    }

    @QueryMapping
    public List<Shelf> shelves() {
        return shelfService.findAll();
    }

    @QueryMapping
    public Shelf shelf(@Argument Long id) {
        return shelfService.getOrThrow(id);
    }

    @QueryMapping
    public List<BookResponse> searchBooks(@Argument String query, @Argument int offset) {
        return openLibraryService.search(query, offset).stream()
                .map(OpenLibraryMapper::toResponse)
                .toList();
    }

    @MutationMapping
    public Shelf createShelf(@Argument String name, @Argument String description) {
        return shelfService.create(ShelfMapper.toEntity(null, new ShelfRequest(name, description)));
    }

    @SchemaMapping(typeName = "Shelf", field = "entries")
    public List<ShelfEntry> entries(Shelf shelf) {
        return shelfEntryService.findByShelfId(shelf.getId());
    }
}
