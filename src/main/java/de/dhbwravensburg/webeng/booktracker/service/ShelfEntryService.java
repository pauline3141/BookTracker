package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryRequest;
import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.mapper.ShelfEntryMapper;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.repository.BookNoteRepository;
import de.dhbwravensburg.webeng.booktracker.repository.BookRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShelfEntryService {

    private final ShelfEntryRepository shelfEntryRepository;
    private final BookRepository bookRepository;
    private final BookNoteRepository bookNoteRepository;
    private final ShelfService shelfService;

    public ShelfEntryService(ShelfEntryRepository shelfEntryRepository,
                             BookRepository bookRepository,
                             BookNoteRepository bookNoteRepository,
                             ShelfService shelfService) {
        this.shelfEntryRepository = shelfEntryRepository;
        this.bookRepository = bookRepository;
        this.bookNoteRepository = bookNoteRepository;
        this.shelfService = shelfService;
    }

    public List<ShelfEntry> findByShelfId(Long shelfId) {
        shelfService.getOrThrow(shelfId);
        return shelfEntryRepository.findByShelfId(shelfId);
    }

    public ShelfEntry addBook(Long shelfId, ShelfEntryRequest request) {
        Shelf shelf = shelfService.getOrThrow(shelfId);
        var book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", request.bookId()));
        ShelfEntry entry = ShelfEntryMapper.toEntity(shelf, book, request);
        return shelfEntryRepository.save(entry);
    }

    public ShelfEntry updateProgress(Long entryId, int currentPage, int totalPages) {
        ShelfEntry entry = getOwnedEntryOrThrow(entryId);
        entry.setCurrentPage(currentPage);
        entry.setTotalPages(totalPages);
        return shelfEntryRepository.save(entry);
    }

    public ShelfEntry moveToShelf(Long entryId, Long targetShelfId) {
        ShelfEntry entry = getOwnedEntryOrThrow(entryId);
        Shelf targetShelf = shelfService.getOrThrow(targetShelfId);
        entry.setShelf(targetShelf);
        return shelfEntryRepository.save(entry);
    }

    public void removeBook(Long entryId) {
        getOwnedEntryOrThrow(entryId);
        bookNoteRepository.deleteAll(bookNoteRepository.findByShelfEntryId(entryId));
        shelfEntryRepository.deleteById(entryId);
    }

    public ShelfEntry getOwnedEntryOrThrow(Long entryId) {
        ShelfEntry entry = shelfEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("ShelfEntry", entryId));
        shelfService.getOrThrow(entry.getShelf().getId());
        return entry;
    }
}