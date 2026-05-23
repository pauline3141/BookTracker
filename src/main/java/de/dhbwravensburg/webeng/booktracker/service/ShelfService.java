package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShelfService {

    private final ShelfRepository repository;

    public ShelfService(ShelfRepository repository) {
        this.repository = repository;
    }

    public List<Shelf> findAll() {
        return repository.findAll();
    }

    public Optional<Shelf> findById(Long id) {
        return repository.findById(id);
    }

    public Shelf create(Shelf shelf) {
        if (shelf.getCreatedAt() == null) {
            shelf.setCreatedAt(LocalDateTime.now());
        }
        return repository.save(shelf);
    }

    public Optional<Shelf> update(Long id, Shelf shelf) {
        return repository.findById(id).map(existing -> {
            existing.setName(shelf.getName());
            existing.setDescription(shelf.getDescription());
            // createdAt bleibt unverändert
            return repository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}