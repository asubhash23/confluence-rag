package dev.subhash.ai.confluence_rag.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.subhash.ai.confluence_rag.model.DocumentChunk;
import dev.subhash.ai.confluence_rag.service.LLMClientService;
import dev.subhash.ai.confluence_rag.service.VectorSearchService;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final VectorSearchService vectorSearchService;
    private final LLMClientService llmClientService;

    public ChatController(VectorSearchService vectorSearchService, LLMClientService llmClientService) {
        this.vectorSearchService = vectorSearchService;
        this.llmClientService = llmClientService;
    }

    // Blocking, full response
    @GetMapping
    public String chat(@RequestParam("query") String query) {
        List<DocumentChunk> context = vectorSearchService.searchSimilarChunks(query, 3);
        return llmClientService.getAnswer(query, context);
    }

    // Streaming with proper formatting
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam("query") String query) {
        List<DocumentChunk> context = vectorSearchService.searchSimilarChunks(query, 3);
        return llmClientService.streamAnswer(query, context);
    }
}