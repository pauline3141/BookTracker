package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfEntryRequest;
import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.model.BookNote;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.repository.BookNoteRepository;
import de.dhbwravensburg.webeng.booktracker.repository.BookRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfEntryRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfEntryServiceTest {

    @Mock
    private ShelfEntryRepository shelfEntryRepository;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookNoteRepository bookNoteRepository;

    @InjectMocks
    private ShelfEntryService service;

    @Test
    void addBook_throwsResourceNotFoundException_whenShelfMissing() {
        ShelfEntryRequest request = new ShelfEntryRequest(1L, 0);
        when(shelfRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addBook(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Shelf")
                .hasMessageContaining("99");
    }

    @Test
    void addBook_throwsResourceNotFoundException_whenBookMissing() {
        Shelf shelf = new Shelf(1L, "Wunschliste", null, null, null);
        ShelfEntryRequest request = new ShelfEntryRequest(99L, 0);
        when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addBook(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book")
                .hasMessageContaining("99");
    }

    @Test
    void addBook_savesEntry_whenShelfAndBookExist() {
        Shelf shelf = new Shelf(1L, "Wunschliste", null, null, null);
        Book book = new Book(2L, "1984", "George Orwell", null, null, 1949, 328);
        ShelfEntryRequest request = new ShelfEntryRequest(2L, 328);
        when(shelfRepository.findById(1L)).thenReturn(Optional.of(shelf));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(shelfEntryRepository.save(any(ShelfEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        ShelfEntry result = service.addBook(1L, request);

        assertThat(result.getShelf()).isEqualTo(shelf);
        assertThat(result.getBook()).isEqualTo(book);
        assertThat(result.getTotalPages()).isEqualTo(328);
    }

    @Test
    void updateProgress_throwsResourceNotFoundException_whenEntryMissing() {
        when(shelfEntryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProgress(99L, 50, 300))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ShelfEntry");
    }

    @Test
    void updateProgress_updatesPages_whenEntryExists() {
        ShelfEntry entry = new ShelfEntry(1L, null, null, null, 0, 0);
        when(shelfEntryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(shelfEntryRepository.save(any(ShelfEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        ShelfEntry result = service.updateProgress(1L, 100, 300);

        assertThat(result.getCurrentPage()).isEqualTo(100);
        assertThat(result.getTotalPages()).isEqualTo(300);
    }

    @Test
    void moveToShelf_throwsResourceNotFoundException_whenTargetShelfMissing() {
        ShelfEntry entry = new ShelfEntry(1L, null, null, null, 0, 0);
        when(shelfEntryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(shelfRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.moveToShelf(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Shelf");
    }

    @Test
    void removeBook_throwsResourceNotFoundException_whenNotFound() {
        when(shelfEntryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.removeBook(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ShelfEntry");
    }

    @Test
    void removeBook_deletesNotesBeforeDeletingEntry_whenFound() {
        BookNote note = new BookNote(10L, null, 5, "Test", LocalDateTime.now());
        when(shelfEntryRepository.existsById(1L)).thenReturn(true);
        when(bookNoteRepository.findByShelfEntryId(1L)).thenReturn(List.of(note));

        service.removeBook(1L);

        verify(bookNoteRepository).deleteAll(List.of(note));
        verify(shelfEntryRepository).deleteById(1L);
    }
}