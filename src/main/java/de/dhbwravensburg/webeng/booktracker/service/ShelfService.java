package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.exception.InvalidUserContextException;
import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.model.User;
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

    public ShelfService(ShelfRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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
        repository.deleteById(id);
    }
}