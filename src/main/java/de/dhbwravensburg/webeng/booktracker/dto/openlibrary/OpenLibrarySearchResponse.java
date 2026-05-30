package de.dhbwravensburg.webeng.booktracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibrarySearchResponse(
        @JsonProperty("numFound") int numFound,
        @JsonProperty("docs") List<OpenLibraryDoc> docs
) {}
