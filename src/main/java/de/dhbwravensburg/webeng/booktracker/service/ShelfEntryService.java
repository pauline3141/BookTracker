package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryRequest;
import de.dhbwravensburg.webeng.booktracker.mapper.ShelfEntryMapper;
import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.model.ReadingStatus;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.repository.BookRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfEntryRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShelfEntryService {

    private final ShelfEntryRepository shelfEntryRepository;
    private final ShelfRepository shelfRepository;
    private final BookRepository bookRepository;

    public ShelfEntryService(ShelfEntryRepository shelfEntryRepository,
                             ShelfRepository shelfRepository,
                             BookRepository bookRepository) {
        this.shelfEntryRepository = shelfEntryRepository;
        this.shelfRepository = shelfRepository;
        this.bookRepository = bookRepository;
    }

    public List<ShelfEntry> findByShelfId(Long shelfId) {
        return shelfEntryRepository.findByShelfId(shelfId);
    }

    public Optional<ShelfEntry> addBook(Long shelfId, ShelfEntryRequest request) {
        Optional<Shelf> shelf = shelfRepository.findById(shelfId);
        Optional<Book> book = bookRepository.findById(request.bookId());

        if (shelf.isEmpty() || book.isEmpty()) {
            return Optional.empty();
        }

        ShelfEntry entry = ShelfEntryMapper.toEntity(shelf.get(), book.get(), request);
        return Optional.of(shelfEntryRepository.save(entry));
    }

    public Optional<ShelfEntry> updateStatus(Long entryId, ReadingStatus status) {
        return shelfEntryRepository.findById(entryId).map(entry -> {
            entry.setStatus(status);
            return shelfEntryRepository.save(entry);
        });
    }

    public boolean removeBook(Long entryId) {
        if (!shelfEntryRepository.existsById(entryId)) {
            return false;
        }
        shelfEntryRepository.deleteById(entryId);
        return true;
    }
}
