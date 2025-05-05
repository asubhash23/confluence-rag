package dev.subhash.ai.confluence_rag.service;

import org.springframework.stereotype.Service;

import dev.subhash.ai.confluence_rag.model.DocumentChunk;
import dev.subhash.ai.confluence_rag.repository.DocumentChunkRepository;

import java.util.*;

@Service
public class ChunkIngestionService {

    private final EmbeddingService embeddingService;
    private final DocumentChunkRepository repository;

    public ChunkIngestionService(EmbeddingService embeddingService, DocumentChunkRepository repository) {
        this.embeddingService = embeddingService;
        this.repository = repository;
    }

    public void ingest(String text, Map<String, String> metadata) {
        List<Double> embedding = embeddingService.getEmbedding(text);
        DocumentChunk chunk = new DocumentChunk(text, metadata, embedding);
        repository.save(chunk);
    }
}

