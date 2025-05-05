package dev.subhash.ai.confluence_rag.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dev.subhash.ai.confluence_rag.model.DocumentChunk;

import com.mongodb.client.AggregateIterable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VectorSearchService {

    private final MongoClient mongoClient;
    private final EmbeddingService embeddingService;
    @Value("${spring.data.mongodb.database}")
    private String databaseName ; 
    private final String collectionName = "chunks";

    public VectorSearchService(MongoClient mongoClient, EmbeddingService embeddingService) {
        this.mongoClient = mongoClient;
        this.embeddingService = embeddingService;
    }

    public List<DocumentChunk> searchSimilarChunks(String queryText, int topN) {
        // 1. Embed the query text
        List<Double> embedding = embeddingService.getEmbedding(queryText);

        // 2. Get MongoDB collection
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        // 3. Prepare $vectorSearch query
        Bson vectorSearchStage = new Document("$vectorSearch",
            new Document("queryVector", embedding)
                .append("path", "embedding")
                .append("numCandidates", 100)
                .append("limit", topN)
                .append("index", "embedding_vector_index")
                .append("similarity", "cosine")
        );

        AggregateIterable<Document> results = collection.aggregate(List.of(vectorSearchStage));

        // 4. Map results to DocumentChunk
        List<DocumentChunk> matchedChunks = new ArrayList<>();
        for (Document doc : results) {
            DocumentChunk chunk = new DocumentChunk();
            chunk.setText(doc.getString("text"));
            chunk.setMetadata((Map<String, String>) doc.get("metadata"));
            matchedChunks.add(chunk);
        }

        return matchedChunks;
    }
}

