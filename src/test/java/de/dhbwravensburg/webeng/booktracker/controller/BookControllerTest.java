package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.mapper.BookMapper;
import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private de.dhbwravensburg.webeng.booktracker.service.OpenLibraryService openLibraryService;

    @Test
    void getAll_returns200WithBooks() throws Exception {
        Book book = new Book(1L, "Der Herr der Ringe", "J.R.R. Tolkien",
                "9780544003415", null, 1954, 1178);
        when(bookService.findAll()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Der Herr der Ringe"))
                .andExpect(jsonPath("$[0].author").value("J.R.R. Tolkien"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(bookService.getOrThrow(99L)).thenThrow(new ResourceNotFoundException("Book", 99L));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    void create_returns400_whenTitleBlank() throws Exception {
        String invalidBody = """
                {
                    "title": "",
                    "author": "Test Autor",
                    "isbn": null,
                    "coverUrl": null,
                    "publishYear": 2020,
                    "totalPages": 300
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation error"))
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        Book saved = new Book(1L, "Neues Buch", "Autor", null, null, 2020, 300);
        when(bookService.create(org.mockito.ArgumentMatchers.any())).thenReturn(saved);

        String validBody = """
                {
                    "title": "Neues Buch",
                    "author": "Autor",
                    "isbn": null,
                    "coverUrl": null,
                    "publishYear": 2020,
                    "totalPages": 300
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content(validBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Neues Buch"));
    }
}