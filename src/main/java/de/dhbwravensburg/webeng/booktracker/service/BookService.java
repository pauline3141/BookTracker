package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.repository.BookNoteRepository;
import de.dhbwravensburg.webeng.booktracker.repository.BookRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;
    private final ShelfEntryRepository shelfEntryRepository;
    private final BookNoteRepository bookNoteRepository;

    public BookService(BookRepository repository,
                       ShelfEntryRepository shelfEntryRepository,
                       BookNoteRepository bookNoteRepository) {
        this.repository = repository;
        this.shelfEntryRepository = shelfEntryRepository;
        this.bookNoteRepository = bookNoteRepository;
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Book getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
    }

    public Book create(Book book) {
        return repository.save(book);
    }

    public Book update(Long id, Book book) {
        Book existing = getOrThrow(id);
        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setIsbn(book.getIsbn());
        existing.setCoverUrl(book.getCoverUrl());
        existing.setPublishYear(book.getPublishYear());
        existing.setTotalPages(book.getTotalPages());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Book", id);
        }

        List<ShelfEntry> entries = shelfEntryRepository.findByBookId(id);
        for (ShelfEntry entry : entries) {
            bookNoteRepository.deleteAll(bookNoteRepository.findByShelfEntryId(entry.getId()));
        }
        shelfEntryRepository.deleteAll(entries);
        repository.deleteById(id);
    }
}