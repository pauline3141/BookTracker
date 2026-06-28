package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

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
    void create_reusesExistingBook_whenIsbnAlreadyExists() {
        Book incoming = new Book(null, "Der Hobbit", "J.R.R. Tolkien",
                "9780261103344", null, 1937, 310);
        Book existing = new Book(5L, "Der Hobbit", "J.R.R. Tolkien",
                "9780261103344", null, 1937, 310);
        when(repository.findByIsbn("9780261103344")).thenReturn(Optional.of(existing));

        Book result = service.create(incoming);

        assertThat(result.getId()).isEqualTo(5L);
        verify(repository, never()).save(any(Book.class));
    }

    @Test
    void create_savesNewBook_whenIsbnIsNew() {
        Book incoming = new Book(null, "Der Hobbit", "J.R.R. Tolkien",
                "9780261103344", null, 1937, 310);
        when(repository.findByIsbn("9780261103344")).thenReturn(Optional.empty());
        when(repository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book result = service.create(incoming);

        assertThat(result.getTitle()).isEqualTo("Der Hobbit");
        verify(repository).save(incoming);
    }

    @Test
    void create_savesNewBook_whenIsbnIsBlank() {
        Book incoming = new Book(null, "Untitled", "Unknown",
                "", null, 0, 0);
        when(repository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book result = service.create(incoming);

        assertThat(result.getTitle()).isEqualTo("Untitled");
        verify(repository, never()).findByIsbn(any());
        verify(repository).save(incoming);
    }
}