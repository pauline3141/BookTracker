package de.dhbwravensburg.webeng.booktracker.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenLibrarySubjectResponse(
        @JsonProperty("works") List<OpenLibrarySubjectWork> works
) {}