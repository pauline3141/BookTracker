package de.dhbwravensburg.webeng.booktracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibrarySubjectWork(
        @JsonProperty("title") String title,
        @JsonProperty("authors") List<OpenLibrarySubjectAuthor> authors,
        @JsonProperty("first_publish_year") Integer firstPublishYear,
        @JsonProperty("cover_id") Integer coverId,
        @JsonProperty("edition_count") Integer editionCount
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OpenLibrarySubjectAuthor(
            @JsonProperty("name") String name
    ) {}
}