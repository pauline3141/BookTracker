package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShelfService {

    private final ShelfRepository repository;

    public ShelfService(ShelfRepository repository) {
        this.repository = repository;
    }

    public List<Shelf> findAll() {
        return repository.findAll();
    }

    public Shelf getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelf", id));
    }

    public Shelf create(Shelf shelf) {
        if (shelf.getCreatedAt() == null) {
            shelf.setCreatedAt(LocalDateTime.now());
        }
        return repository.save(shelf);
    }

    public Shelf update(Long id, Shelf shelf) {
        Shelf existing = getOrThrow(id);
        existing.setName(shelf.getName());
        existing.setDescription(shelf.getDescription());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Shelf", id);
        }
        repository.deleteById(id);
    }
}