package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.model.BookNote;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.repository.BookNoteRepository;
import de.dhbwravensburg.webeng.booktracker.repository.BookRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    @Mock
    private ShelfEntryRepository shelfEntryRepository;

    @Mock
    private BookNoteRepository bookNoteRepository;

    @InjectMocks
    private BookService service;

    @Test
    void getOrThrow_returnsBook_whenFound() {
        Book book = new Book(1L, "Der Herr der Ringe", "J.R.R. Tolkien",
                "9780544003415", null, 1954, 1178);
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        Book result = service.getOrThrow(1L);

        assertThat(result.getTitle()).isEqualTo("Der Herr der Ringe");
        assertThat(result.getAuthor()).isEqualTo("J.R.R. Tolkien");
    }

    @Test
    void getOrThrow_throwsResourceNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOrThrow(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book")
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsBook() {
        Book book = new Book(null, "1984", "George Orwell",
                "9780451524935", null, 1949, 328);
        Book saved = new Book(1L, "1984", "George Orwell",
                "9780451524935", null, 1949, 328);
        when(repository.save(book)).thenReturn(saved);

        Book result = service.create(book);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("1984");
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book")
                .hasMessageContaining("99");
    }

    @Test
    void delete_removesNotesAndEntriesBeforeDeletingBook_whenFound() {
        ShelfEntry entry = new ShelfEntry(5L, null, null, null, 0, 0);
        BookNote note = new BookNote(10L, entry, 5, "Test", LocalDateTime.now());
        when(repository.existsById(1L)).thenReturn(true);
        when(shelfEntryRepository.findByBookId(1L)).thenReturn(List.of(entry));
        when(bookNoteRepository.findByShelfEntryId(5L)).thenReturn(List.of(note));

        service.delete(1L);

        verify(bookNoteRepository).deleteAll(List.of(note));
        verify(shelfEntryRepository).deleteAll(List.of(entry));
        verify(repository).deleteById(1L);
    }
}