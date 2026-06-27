package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.dto.openlibrary.OpenLibraryDoc;
import de.dhbwravensburg.webeng.booktracker.dto.openlibrary.OpenLibrarySearchResponse;
import de.dhbwravensburg.webeng.booktracker.dto.openlibrary.OpenLibrarySubjectResponse;
import de.dhbwravensburg.webeng.booktracker.dto.openlibrary.OpenLibrarySubjectWork;
import de.dhbwravensburg.webeng.booktracker.exception.ExternalApiClientException;
import de.dhbwravensburg.webeng.booktracker.exception.ExternalApiException;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class OpenLibraryService {

    private static final List<String> DISCOVER_SUBJECTS = List.of(
            "fiction", "fantasy", "mystery", "science", "romance",
            "history", "biography", "thriller", "poetry", "philosophy"
    );

    private final RestClient openLibraryRestClient;
    private final Random random = new Random();

    public OpenLibraryService(RestClient openLibraryRestClient) {
        this.openLibraryRestClient = openLibraryRestClient;
    }

    @Retryable(
            retryFor = ExternalApiException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2.0)
    )
    public List<OpenLibraryDoc> search(String query, int offset) {
        try {
            OpenLibrarySearchResponse response = openLibraryRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search.json")
                            .queryParam("q", query)
                            .queryParam("limit", 10)
                            .queryParam("offset", offset)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new ExternalApiClientException(
                                "Open Library client error: " + res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        throw new ExternalApiException(
                                "Open Library server error: " + res.getStatusCode());
                    })
                    .body(OpenLibrarySearchResponse.class);

            if (response == null || response.docs() == null) {
                return Collections.emptyList();
            }

            return response.docs();

        } catch (RestClientException ex) {
            throw new ExternalApiException(
                    "Failed to call Open Library API: " + ex.getMessage(), ex);
        }
    }

    @Retryable(
            retryFor = ExternalApiException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2.0)
    )
    public List<OpenLibrarySubjectWork> discover() {
        String subject = DISCOVER_SUBJECTS.get(random.nextInt(DISCOVER_SUBJECTS.size()));
        try {
            OpenLibrarySubjectResponse response = openLibraryRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/subjects/{subject}.json")
                            .queryParam("limit", 12)
                            .build(subject))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new ExternalApiClientException(
                                "Open Library client error: " + res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        throw new ExternalApiException(
                                "Open Library server error: " + res.getStatusCode());
                    })
                    .body(OpenLibrarySubjectResponse.class);

            if (response == null || response.works() == null) {
                return Collections.emptyList();
            }

            return response.works();

        } catch (RestClientException ex) {
            throw new ExternalApiException(
                    "Failed to call Open Library API: " + ex.getMessage(), ex);
        }
    }
}