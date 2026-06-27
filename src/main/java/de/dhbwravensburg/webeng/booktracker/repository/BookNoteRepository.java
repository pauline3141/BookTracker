package de.dhbwravensburg.webeng.booktracker.repository;

import de.dhbwravensburg.webeng.booktracker.model.BookNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookNoteRepository extends JpaRepository<BookNote, Long> {

    List<BookNote> findByShelfEntryId(Long shelfEntryId);
}