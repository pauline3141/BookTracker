package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.dto.BookNoteRequest;
import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.model.BookNote;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.repository.BookNoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookNoteServiceTest {

    @Mock
    private BookNoteRepository noteRepository;

    @Mock
    private ShelfEntryService shelfEntryService;

    @InjectMocks
    private BookNoteService service;

    @Test
    void addNote_throwsResourceNotFoundException_whenShelfEntryMissing() {
        BookNoteRequest request = new BookNoteRequest(10, "Test note");
        when(shelfEntryService.getOwnedEntryOrThrow(99L))
                .thenThrow(new ResourceNotFoundException("ShelfEntry", 99L));

        assertThatThrownBy(() -> service.addNote(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ShelfEntry")
                .hasMessageContaining("99");
    }

    @Test
    void addNote_savesNote_whenShelfEntryExists() {
        ShelfEntry entry = new ShelfEntry(1L, null, null, null, 0, 0);
        BookNoteRequest request = new BookNoteRequest(42, "Great chapter");
        when(shelfEntryService.getOwnedEntryOrThrow(1L)).thenReturn(entry);
        when(noteRepository.save(any(BookNote.class))).thenAnswer(inv -> inv.getArgument(0));

        BookNote result = service.addNote(1L, request);

        assertThat(result.getContent()).isEqualTo("Great chapter");
        assertThat(result.getPageReference()).isEqualTo(42);
    }

    @Test
    void updateNote_throwsResourceNotFoundException_whenNotFound() {
        BookNoteRequest request = new BookNoteRequest(5, "Updated");
        when(noteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateNote(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("BookNote");
    }

    @Test
    void updateNote_updatesFields_whenFound() {
        ShelfEntry entry = new ShelfEntry(1L, null, null, null, 0, 0);
        BookNote existing = new BookNote(1L, entry, 10, "Old content", LocalDateTime.now());
        BookNoteRequest request = new BookNoteRequest(20, "New content");
        when(noteRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(shelfEntryService.getOwnedEntryOrThrow(1L)).thenReturn(entry);
        when(noteRepository.save(any(BookNote.class))).thenAnswer(inv -> inv.getArgument(0));

        BookNote result = service.updateNote(1L, request);

        assertThat(result.getContent()).isEqualTo("New content");
        assertThat(result.getPageReference()).isEqualTo(20);
    }

    @Test
    void deleteNote_throwsResourceNotFoundException_whenNotFound() {
        when(noteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteNote(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("BookNote");
    }
}