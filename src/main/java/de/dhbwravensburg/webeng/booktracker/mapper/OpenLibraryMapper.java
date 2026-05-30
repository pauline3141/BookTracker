package de.dhbwravensburg.webeng.booktracker.mapper;

import de.dhbwravensburg.webeng.booktracker.dto.BookResponse;
import de.dhbwravensburg.webeng.booktracker.dto.openlibrary.OpenLibraryDoc;

public final class OpenLibraryMapper {

    private OpenLibraryMapper() {}

    public static BookResponse toResponse(OpenLibraryDoc doc) {
        String author = (doc.authorName() != null && !doc.authorName().isEmpty())
                ? doc.authorName().get(0)
                : "Unknown";

        String isbn = (doc.isbn() != null && !doc.isbn().isEmpty())
                ? doc.isbn().get(0)
                : null;

        String coverUrl = doc.coverId() != null
                ? "https://covers.openlibrary.org/b/id/" + doc.coverId() + "-M.jpg"
                : null;

        return new BookResponse(
                null,
                doc.title(),
                author,
                isbn,
                coverUrl,
                doc.firstPublishYear()
        );
    }
}