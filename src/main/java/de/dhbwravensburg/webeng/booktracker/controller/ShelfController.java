package de.dhbwravensburg.webeng.booktracker.controller;

import de.dhbwravensburg.webeng.booktracker.dto.ShelfRequest;
import de.dhbwravensburg.webeng.booktracker.dto.ShelfResponse;
import de.dhbwravensburg.webeng.booktracker.mapper.ShelfMapper;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.service.ShelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/shelves")
@Tag(name = "Shelves", description = "Create and manage user-scoped book shelves")
public class ShelfController {

    private final ShelfService service;

    public ShelfController(ShelfService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all shelves", description = "Returns all shelves belonging to the authenticated user")
    public List<ShelfResponse> getAll() {
        return service.findAll().stream()
                .map(ShelfMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a shelf by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shelf found"),
            @ApiResponse(responseCode = "404", description = "Shelf not found or not owned by the user")
    })
    public ShelfResponse getById(
            @Parameter(description = "ID of the shelf") @PathVariable Long id) {
        return ShelfMapper.toResponse(service.getOrThrow(id));
    }

    @PostMapping
    @Operation(summary = "Create a new shelf")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Shelf created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<ShelfResponse> create(@Valid @RequestBody ShelfRequest request) {
        Shelf created = service.create(ShelfMapper.toEntity(null, request));
        ShelfResponse response = ShelfMapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/shelves/" + created.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing shelf")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shelf updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Shelf not found or not owned by the user")
    })
    public ShelfResponse update(
            @Parameter(description = "ID of the shelf") @PathVariable Long id,
            @Valid @RequestBody ShelfRequest request) {
        Shelf updated = service.update(id, ShelfMapper.toEntity(id, request));
        return ShelfMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a shelf", description = "Cascades to all entries and notes on the shelf")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Shelf deleted"),
            @ApiResponse(responseCode = "404", description = "Shelf not found or not owned by the user")
    })
    public void delete(
            @Parameter(description = "ID of the shelf") @PathVariable Long id) {
        service.delete(id);
    }
}