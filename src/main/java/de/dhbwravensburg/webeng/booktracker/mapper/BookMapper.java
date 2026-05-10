package de.dhbwravensburg.webeng.booktracker.mapper;

import de.dhbwravensburg.webeng.booktracker.dto.BookRequest;
import de.dhbwravensburg.webeng.booktracker.dto.BookResponse;
import de.dhbwravensburg.webeng.booktracker.model.Book;

public final class BookMapper {

    private BookMapper() {}

    public static Book toEntity(Long id, BookRequest request) {
        return new Book(
                id,
                request.title(),
                request.author(),
                request.isbn(),
                request.coverUrl(),
                request.publishYear()
        );
    }

    public static BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCoverUrl(),
                book.getPublishYear()
        );
    }
}
