package de.dhbwravensburg.webeng.booktracker.service;

import de.dhbwravensburg.webeng.booktracker.exception.ExternalApiClientException;
import de.dhbwravensburg.webeng.booktracker.exception.ExternalApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenLibraryServiceTest {

    @Test
    void search_throwsExternalApiException_on5xxResponse() {
        RestClient.RequestHeadersUriSpec requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        RestClient restClient = mock(RestClient.class);

        when(restClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri(any(java.util.function.Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(Class.class)))
                .thenThrow(new ExternalApiException("Open Library server error: 500 INTERNAL_SERVER_ERROR"));

        OpenLibraryService service = new OpenLibraryService(restClient);

        assertThatThrownBy(() -> service.search("tolkien", 0))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("Open Library server error");
    }

    @Test
    void search_throwsExternalApiException_onNetworkError() {
        RestClient.RequestHeadersUriSpec requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        RestClient restClient = mock(RestClient.class);

        when(restClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri(any(java.util.function.Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(Class.class)))
                .thenThrow(new org.springframework.web.client.RestClientException("Connection refused"));

        OpenLibraryService service = new OpenLibraryService(restClient);

        assertThatThrownBy(() -> service.search("tolkien", 0))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("Failed to call Open Library API");
    }

    @Test
    void search_throwsClientException_on4xxResponse() {
        RestClient.RequestHeadersUriSpec requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        RestClient restClient = mock(RestClient.class);

        when(restClient.get()).thenReturn(requestSpec);
        when(requestSpec.uri(any(java.util.function.Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(any(Class.class)))
                .thenThrow(new ExternalApiClientException("Open Library client error: 404 NOT_FOUND"));

        OpenLibraryService service = new OpenLibraryService(restClient);

        assertThatThrownBy(() -> service.search("tolkien", 0))
                .isInstanceOf(ExternalApiClientException.class)
                .hasMessageContaining("Open Library client error");
    }
}