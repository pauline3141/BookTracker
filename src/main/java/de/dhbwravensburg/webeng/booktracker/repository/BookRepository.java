package de.dhbwravensburg.webeng.booktracker.repository;

import de.dhbwravensburg.webeng.booktracker.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}