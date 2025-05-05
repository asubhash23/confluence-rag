package dev.subhash.ai.confluence_rag.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Document("chunks")
public class DocumentChunk {

    @Id
    private String id;

    private String text;
    private Map<String, String> metadata;
    private List<Double> embedding;


    public DocumentChunk() {}

    public DocumentChunk(String text, Map<String, String> metadata, List<Double> embedding) {
        this.text = text;
        this.metadata = metadata;
        this.embedding = embedding;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }

    
}

