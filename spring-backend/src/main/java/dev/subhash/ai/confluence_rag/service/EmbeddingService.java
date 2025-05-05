package dev.subhash.ai.confluence_rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class EmbeddingService {

    @Value("${app.embedding-service.url}")
    private String embedServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Double> getEmbedding(String text) {
        Map<String, Object> requestBody = Map.of("texts", List.of(text));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        Map<String, List<List<Double>>> response = restTemplate
                .postForObject(embedServiceUrl + "/embed", request, Map.class);

        return response.get("embeddings").get(0); // only one embedding
    }
}

