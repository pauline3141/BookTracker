package de.dhbwravensburg.webeng.booktracker.repository;

import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
}