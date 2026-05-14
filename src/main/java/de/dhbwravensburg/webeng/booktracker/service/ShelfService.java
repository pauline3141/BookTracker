package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ShelfService {

    private final ConcurrentHashMap<Long, Shelf> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ShelfService() {

        create(new Shelf(null, "Wunschliste", "Bücher auf meiner Wunschliste", null));
        create(new Shelf(null, "Aktuell", "Bücher, die ich aktuell lese", null));
        create(new Shelf(null, "Gelesen", "Fertig gelesene Bücher", null));
    }

    public List<Shelf> findAll() {
        return List.copyOf(store.values());
    }

    public Optional<Shelf> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Shelf create(Shelf shelf) {
        Long newId = idGenerator.getAndIncrement();
        shelf.setId(newId);
        if (shelf.getCreatedAt() == null) {
            shelf.setCreatedAt(LocalDateTime.now());
        }
        store.put(newId, shelf);
        return shelf;
    }

    public Optional<Shelf> update(Long id, Shelf shelf) {
        if (!store.containsKey(id)) {
            return Optional.empty();
        }
        shelf.setId(id);
        shelf.setCreatedAt(store.get(id).getCreatedAt());
        store.put(id, shelf);
        return Optional.of(shelf);
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}