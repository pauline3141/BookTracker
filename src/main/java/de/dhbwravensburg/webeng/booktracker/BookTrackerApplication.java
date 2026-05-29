package de.dhbwravensburg.webeng.booktracker;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
        title = "BookTracker API",
        version = "0.1.0",
        description = "Search for books, add them to shelves, and track the reading progress",
        contact = @Contact(name = "DHBW Web-Engineering", email = "noreply@dhbw.de")
))
@SpringBootApplication
public class BookTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookTrackerApplication.class, args);
    }
}