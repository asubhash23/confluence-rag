package dev.subhash.ai.confluence_rag.controller;

import org.springframework.web.bind.annotation.*;

import dev.subhash.ai.confluence_rag.model.DocumentChunk;
import dev.subhash.ai.confluence_rag.service.VectorSearchService;

import java.util.List;

@RestController
@RequestMapping("/search")
public class VectorSearchController {

    private final VectorSearchService vectorSearchService;

    public VectorSearchController(VectorSearchService vectorSearchService) {
        this.vectorSearchService = vectorSearchService;
    }

    @GetMapping
    public List<DocumentChunk> search(@RequestParam("query") String query) {
        return vectorSearchService.searchSimilarChunks(query, 5);
    }
}

