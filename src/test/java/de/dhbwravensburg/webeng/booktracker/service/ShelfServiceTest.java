package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.model.BookNote;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.model.ShelfEntry;
import de.dhbwravensburg.webeng.booktracker.model.User;
import de.dhbwravensburg.webeng.booktracker.repository.BookNoteRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfEntryRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfRepository;
import de.dhbwravensburg.webeng.booktracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @Mock
    private ShelfRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShelfEntryRepository shelfEntryRepository;

    @Mock
    private BookNoteRepository bookNoteRepository;

    @InjectMocks
    private ShelfService service;

    private void mockCurrentUser(User user) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(user.getUsername());
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    }

    @Test
    void getOrThrow_returnsShelf_whenFound() {
        User user = new User(1L, "testuser", "secret");
        mockCurrentUser(user);
        Shelf shelf = new Shelf(1L, "Wunschliste", "Meine Wunschliste", LocalDateTime.now(), user);
        when(repository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));

        Shelf result = service.getOrThrow(1L);

        assertThat(result.getName()).isEqualTo("Wunschliste");
    }

    @Test
    void getOrThrow_throwsResourceNotFoundException_whenNotFound() {
        User user = new User(1L, "testuser", "secret");
        mockCurrentUser(user);
        when(repository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOrThrow(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Shelf")
                .hasMessageContaining("99");
    }

    @Test
    void create_setsCreatedAt_whenNull() {
        User user = new User(1L, "testuser", "secret");
        mockCurrentUser(user);
        Shelf shelf = new Shelf(null, "Aktuell", "Lese gerade", null, null);
        when(repository.save(any(Shelf.class))).thenAnswer(inv -> inv.getArgument(0));

        Shelf result = service.create(shelf);

        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void create_keepsCreatedAt_whenAlreadySet() {
        User user = new User(1L, "testuser", "secret");
        mockCurrentUser(user);
        LocalDateTime fixed = LocalDateTime.of(2024, 1, 1, 0, 0);
        Shelf shelf = new Shelf(null, "Gelesen", "Fertig gelesen", fixed, null);
        when(repository.save(any(Shelf.class))).thenAnswer(inv -> inv.getArgument(0));

        Shelf result = service.create(shelf);

        assertThat(result.getCreatedAt()).isEqualTo(fixed);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        User user = new User(1L, "testuser", "secret");
        mockCurrentUser(user);
        when(repository.existsByIdAndUserId(99L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Shelf");
    }

    @Test
    void delete_removesNotesAndEntriesBeforeDeletingShelf_whenFound() {
        User user = new User(1L, "testuser", "secret");
        mockCurrentUser(user);
        ShelfEntry entry = new ShelfEntry(5L, null, null, null, 0, 0);
        BookNote note = new BookNote(10L, entry, 5, "Test", LocalDateTime.now());
        when(repository.existsByIdAndUserId(1L, 1L)).thenReturn(true);
        when(shelfEntryRepository.findByShelfId(1L)).thenReturn(List.of(entry));
        when(bookNoteRepository.findByShelfEntryId(5L)).thenReturn(List.of(note));

        service.delete(1L);

        verify(bookNoteRepository).deleteAll(List.of(note));
        verify(shelfEntryRepository).deleteAll(List.of(entry));
        verify(repository).deleteById(1L);
    }
}