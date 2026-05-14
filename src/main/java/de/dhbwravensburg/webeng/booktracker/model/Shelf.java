package de.dhbwravensburg.webeng.booktracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shelf {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}