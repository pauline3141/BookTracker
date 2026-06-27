package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.exception.InvalidUserContextException;
import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.model.User;
import de.dhbwravensburg.webeng.booktracker.repository.BookNoteRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfEntryRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfRepository;
import de.dhbwravensburg.webeng.booktracker.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShelfService {

    private final ShelfRepository repository;
    private final UserRepository userRepository;
    private final ShelfEntryRepository shelfEntryRepository;
    private final BookNoteRepository bookNoteRepository;

    public ShelfService(ShelfRepository repository,
                        UserRepository userRepository,
                        ShelfEntryRepository shelfEntryRepository,
                        BookNoteRepository bookNoteRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.shelfEntryRepository = shelfEntryRepository;
        this.bookNoteRepository = bookNoteRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidUserContextException(username));
    }

    public List<Shelf> findAll() {
        return repository.findByUserId(getCurrentUser().getId());
    }

    public Shelf getOrThrow(Long id) {
        return repository.findByIdAndUserId(id, getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Shelf", id));
    }

    public Shelf create(Shelf shelf) {
        if (shelf.getCreatedAt() == null) {
            shelf.setCreatedAt(LocalDateTime.now());
        }
        shelf.setUser(getCurrentUser());
        return repository.save(shelf);
    }

    public Shelf update(Long id, Shelf shelf) {
        Shelf existing = getOrThrow(id);
        existing.setName(shelf.getName());
        existing.setDescription(shelf.getDescription());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsByIdAndUserId(id, getCurrentUser().getId())) {
            throw new ResourceNotFoundException("Shelf", id);
        }

        List<ShelfEntry> entries = shelfEntryRepository.findByShelfId(id);
        for (ShelfEntry entry : entries) {
            bookNoteRepository.deleteAll(bookNoteRepository.findByShelfEntryId(entry.getId()));
        }
        shelfEntryRepository.deleteAll(entries);
        repository.deleteById(id);
    }
}