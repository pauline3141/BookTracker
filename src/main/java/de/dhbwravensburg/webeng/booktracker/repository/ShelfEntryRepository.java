package de.dhbwravensburg.webeng.booktracker.repository;

import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShelfEntryRepository extends JpaRepository<ShelfEntry, Long> {

    List<ShelfEntry> findByShelfId(Long shelfId);
}
