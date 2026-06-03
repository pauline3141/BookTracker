package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.dto.openlibrary.OpenLibraryDoc;
import de.dhbwravensburg.webeng.booktracker.dto.openlibrary.OpenLibrarySearchResponse;
import de.dhbwravensburg.webeng.booktracker.exception.ExternalApiException;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

@Service
public class OpenLibraryService {

    private final RestClient openLibraryRestClient;

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
                        throw new ExternalApiException(
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
}