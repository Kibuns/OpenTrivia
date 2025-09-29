package com.nino.opentrivia.client;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Profile("!stub")
public class LiveOpenTdbClient implements OpenTdbClient {

    private final RestTemplate restTemplate;

    public LiveOpenTdbClient() {
        this.restTemplate = new RestTemplate();
    }

    private record OpenTdbResponse(
            int response_code,
            List<OpenTdbQuestion> results
    ) {}

    @Override
    public List<OpenTdbQuestion> fetchQuestions(int amount, String category, String difficulty) {
        // Basis URL
        String baseUrl = "https://opentdb.com/api.php?amount=" + amount;

        if (category != null && !category.isBlank()) {
            baseUrl += "&category=" + category;
        }
        if (difficulty != null && !difficulty.isBlank()) {
            baseUrl += "&difficulty=" + difficulty;
        }

        // API-call
        ResponseEntity<OpenTdbResponse> response =
                restTemplate.getForEntity(baseUrl, OpenTdbResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("OpenTDB API returned an error");
        }

        return response.getBody().results();
    }
}