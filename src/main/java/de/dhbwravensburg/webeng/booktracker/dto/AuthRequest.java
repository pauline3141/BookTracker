package de.dhbwravensburg.webeng.booktracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "username must not be blank")
        @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "password must not be blank")
        @Size(min = 6, message = "password must be at least 6 characters")
        String password
) {}