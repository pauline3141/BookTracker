package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final List<Book> books = List.of(
            new Book(1L, "Der Herr der Ringe", "J.R.R. Tolkien",
                    "9780544003415", null, 1954),
            new Book(2L, "Der Schatten des Windes", "Carlos Ruiz Zafón",
                    "9783596196159", null, 2001),
            new Book(3L, "1984", "George Orwell",
                    "9780451524935", null, 1949)
    );

    @GetMapping
    public List<Book> getAll() {
        return books;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}