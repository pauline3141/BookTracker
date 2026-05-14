package de.dhbwravensburg.webeng.booktracker.config;

import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedBooks(BookRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.save(new Book(null, "Der Herr der Ringe", "J.R.R. Tolkien",
                    "9780544003415", null, 1954));
            repository.save(new Book(null, "Der Schatten des Windes", "Carlos Ruiz Zafón",
                    "9783596196159", null, 2001));
            repository.save(new Book(null, "1984", "George Orwell",
                    "9780451524935", null, 1949));
        };
    }
}