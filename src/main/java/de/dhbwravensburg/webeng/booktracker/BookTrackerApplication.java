package de.dhbwravensburg.webeng.booktracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class BookTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookTrackerApplication.class, args);
    }
}