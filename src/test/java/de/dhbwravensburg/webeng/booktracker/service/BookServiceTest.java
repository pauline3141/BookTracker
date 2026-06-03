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
import static org.mockito.Mockito.when;

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
}