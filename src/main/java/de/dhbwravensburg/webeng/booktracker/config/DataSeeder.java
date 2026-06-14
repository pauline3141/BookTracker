package de.dhbwravensburg.webeng.booktracker.config;

import de.dhbwravensburg.webeng.booktracker.model.Book;
import de.dhbwravensburg.webeng.booktracker.model.Shelf;
import de.dhbwravensburg.webeng.booktracker.model.User;
import de.dhbwravensburg.webeng.booktracker.repository.BookRepository;
import de.dhbwravensburg.webeng.booktracker.repository.ShelfRepository;
import de.dhbwravensburg.webeng.booktracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(BookRepository bookRepository,
                               ShelfRepository shelfRepository,
                               UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            User demoUser;
            if (userRepository.count() == 0) {
                demoUser = userRepository.save(
                        new User(null, "demo", passwordEncoder.encode("demo123")));
            } else {
                demoUser = userRepository.findByUsername("demo").orElseThrow();
            }

            if (bookRepository.count() == 0) {
                bookRepository.save(new Book(null, "Der Herr der Ringe", "J.R.R. Tolkien",
                        "9780544003415", null, 1954, 1178));
                bookRepository.save(new Book(null, "Der Schatten des Windes", "Carlos Ruiz Zafón",
                        "9783596196159", null, 2001, 566));
                bookRepository.save(new Book(null, "1984", "George Orwell",
                        "9780451524935", null, 1949, 328));
            }

            if (shelfRepository.count() == 0) {
                LocalDateTime now = LocalDateTime.now();
                shelfRepository.save(new Shelf(null, "Wunschliste",
                        "Bücher auf meiner Wunschliste", now, demoUser));
                shelfRepository.save(new Shelf(null, "Aktuell",
                        "Bücher, die ich aktuell lese", now, demoUser));
                shelfRepository.save(new Shelf(null, "Gelesen",
                        "Fertig gelesene Bücher", now, demoUser));
            }
        };
    }
}