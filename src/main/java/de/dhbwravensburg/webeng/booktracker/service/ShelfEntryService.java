package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryRequest;
import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.mapper.ShelfEntryMapper;
import de.dhbwravensburg.webeng.booktracker.model.ReadingStatus;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.repository.BookRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfEntryRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public ShelfEntry addBook(Long shelfId, ShelfEntryRequest request) {
        var shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new ResourceNotFoundException("Shelf", shelfId));
        var book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", request.bookId()));

        ShelfEntry entry = ShelfEntryMapper.toEntity(shelf, book, request);
        return shelfEntryRepository.save(entry);
    }

    public ShelfEntry updateStatus(Long entryId, ReadingStatus status) {
        ShelfEntry entry = shelfEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("ShelfEntry", entryId));
        entry.setStatus(status);
        return shelfEntryRepository.save(entry);
    }

    public ShelfEntry updateProgress(Long entryId, int currentPage, int totalPages) {
        ShelfEntry entry = shelfEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("ShelfEntry", entryId));
        entry.setCurrentPage(currentPage);
        entry.setTotalPages(totalPages);
        return shelfEntryRepository.save(entry);
    }

    public void removeBook(Long entryId) {
        if (!shelfEntryRepository.existsById(entryId)) {
            throw new ResourceNotFoundException("ShelfEntry", entryId);
        }
        shelfEntryRepository.deleteById(entryId);
    }
}