package dev.subhash.ai.confluence_rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.subhash.ai.confluence_rag.model.DocumentChunk;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class LLMClientService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

  

    public LLMClientService(@Value("${azure.ollama.url}") String ollamaUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(ollamaUrl)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }

    public String getAnswer(String query, List<DocumentChunk> contextChunks) {
        return streamAnswer(query, contextChunks)
            .collect(Collectors.joining("")) // Preserve exact formatting
            .block();
    }

    public Flux<String> streamAnswer(String query, List<DocumentChunk> contextChunks) {
        String prompt = buildPrompt(query, contextChunks);
    
        Map<String, Object> payload = Map.of(
            "model", "mistral",
            "stream", true,
            "messages", List.of(
                Map.of("role", "user", "content", prompt)
            )
        );
    
        return webClient.post()
            .uri("/api/chat")
            .bodyValue(payload)
            .retrieve()
            .bodyToFlux(String.class)
            .map(this::extractContent)
            .filter(Objects::nonNull)
            .bufferUntil(token -> token.matches(".*[.!?]$")) // 🔹 buffer till sentence ends
            .map(this::joinTokensWithSpacing);               // 🔹 format sentence properly
    }
    
    private String joinTokensWithSpacing(List<String> tokens) {
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (sb.length() > 0 &&
                !sb.toString().endsWith(" ") &&
                !token.matches("^[.,!?;:]$")) {
                sb.append(" ");
            }
            sb.append(token);
        }
        return sb.toString();
    }

    private String buildPrompt(String query, List<DocumentChunk> contextChunks) {
        StringBuilder prompt = new StringBuilder("Use the following context to answer the question:\n\n");
        for (DocumentChunk chunk : contextChunks) {
            prompt.append("- ").append(chunk.getText()).append("\n");
        }
        prompt.append("\nQuestion: ").append(query);
        return prompt.toString();
    }

    private String extractContent(String ndjsonLine) {
        try {
            Map<String, Object> parsed = objectMapper.readValue(ndjsonLine, Map.class);
            Map<String, Object> message = (Map<String, Object>) parsed.get("message");
            String content = message != null ? (String) message.get("content") : null;
            System.out.println("TOKEN: [" + content + "]");
            return content;
        } catch (Exception e) {
            System.err.println("Error parsing: " + ndjsonLine);
            return null;
        }
    }
       
}