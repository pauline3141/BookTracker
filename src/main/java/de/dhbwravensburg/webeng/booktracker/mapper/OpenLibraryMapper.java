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

        int totalPages = 0;
        if (doc.numberOfPagesMedian() != null && doc.numberOfPagesMedian() > 0) {
            totalPages = doc.numberOfPagesMedian();
        } else if (doc.numberOfPages() != null && doc.numberOfPages() > 0) {
            totalPages = doc.numberOfPages();
        }

        return new BookResponse(
                null,
                doc.title(),
                author,
                isbn,
                coverUrl,
                doc.firstPublishYear() != null ? doc.firstPublishYear() : 0,
                totalPages
        );
    }
}