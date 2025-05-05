package dev.subhash.ai.confluence_rag.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import dev.subhash.ai.confluence_rag.model.DocumentChunk;

public interface DocumentChunkRepository extends MongoRepository<DocumentChunk, String> {
}

