package de.dhbwravensburg.webeng.booktracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class OpenLibraryApiConfig {

    @Value("${openlibrary.api.base-url}")
    private String baseUrl;

    @Value("${openlibrary.api.connect-timeout-ms}")
    private int connectTimeoutMs;

    @Value("${openlibrary.api.read-timeout-ms}")
    private int readTimeoutMs;

    @Bean
    public RestClient openLibraryRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        factory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
