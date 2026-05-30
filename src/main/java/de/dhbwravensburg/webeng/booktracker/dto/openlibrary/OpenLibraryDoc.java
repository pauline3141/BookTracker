package de.dhbwravensburg.webeng.booktracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibraryDoc(
        @JsonProperty("title") String title,
        @JsonProperty("author_name") List<String> authorName,
        @JsonProperty("isbn") List<String> isbn,
        @JsonProperty("first_publish_year") Integer firstPublishYear,
        @JsonProperty("cover_i") Integer coverId
) {}
