package de.dhbwravensburg.webeng.booktracker.config;

import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.repository.BookRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(BookRepository bookRepository,
                               ShelfRepository shelfRepository) {
        return args -> {
            if (bookRepository.count() == 0) {
                bookRepository.save(new Book(null, "Der Herr der Ringe", "J.R.R. Tolkien",
                        "9780544003415", null, 1954));
                bookRepository.save(new Book(null, "Der Schatten des Windes", "Carlos Ruiz Zafón",
                        "9783596196159", null, 2001));
                bookRepository.save(new Book(null, "1984", "George Orwell",
                        "9780451524935", null, 1949));
            }

            if (shelfRepository.count() == 0) {
                LocalDateTime now = LocalDateTime.now();
                shelfRepository.save(new Shelf(null, "Wunschliste",
                        "Bücher auf meiner Wunschliste", now));
                shelfRepository.save(new Shelf(null, "Aktuell",
                        "Bücher, die ich aktuell lese", now));
                shelfRepository.save(new Shelf(null, "Gelesen",
                        "Fertig gelesene Bücher", now));
            }
        };
    }
}