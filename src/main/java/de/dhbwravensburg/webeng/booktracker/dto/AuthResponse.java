package de.dhbwravensburg.webeng.booktracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @Schema(description = "Signed JWT to be sent as a Bearer token on subsequent requests")
        String token,

        @Schema(description = "Username the token belongs to", example = "demo")
        String username
) {}