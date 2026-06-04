package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.exception.ResourceNotFoundException;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.service.ShelfService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShelfController.class)
class ShelfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShelfService shelfService;

    @Test
    void getAll_returns200WithShelves() throws Exception {
        Shelf shelf = new Shelf(1L, "Wunschliste", "Meine Wunschliste", LocalDateTime.now());
        when(shelfService.findAll()).thenReturn(List.of(shelf));

        mockMvc.perform(get("/api/shelves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Wunschliste"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(shelfService.getOrThrow(99L)).thenThrow(new ResourceNotFoundException("Shelf", 99L));

        mockMvc.perform(get("/api/shelves/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    void create_returns400_whenNameBlank() throws Exception {
        String invalidBody = """
                {
                    "name": "",
                    "description": "Test"
                }
                """;

        mockMvc.perform(post("/api/shelves")
                        .contentType("application/json")
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation error"))
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }
}