package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookService {

    private final ConcurrentHashMap<Long, Book> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public BookService() {
        // Seed data for development
        create(new Book(null, "Der Herr der Ringe", "J.R.R. Tolkien",
                "9780544003415", null, 1954));
        create(new Book(null, "Der Schatten des Windes", "Carlos Ruiz Zafón",
                "9783596196159", null, 2001));
        create(new Book(null, "1984", "George Orwell",
                "9780451524935", null, 1949));
    }

    public List<Book> findAll() {
        return List.copyOf(store.values());
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Book create(Book book) {
        Long newId = idGenerator.getAndIncrement();
        book.setId(newId);
        store.put(newId, book);
        return book;
    }

    public Optional<Book> update(Long id, Book book) {
        if (!store.containsKey(id)) {
            return Optional.empty();
        }
        book.setId(id);
        store.put(id, book);
        return Optional.of(book);
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}