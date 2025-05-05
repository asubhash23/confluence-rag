package dev.subhash.ai.confluence_rag.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.subhash.ai.confluence_rag.dto.IngestRequest;
import dev.subhash.ai.confluence_rag.service.ChunkIngestionService;

@RestController
public class IngestController {

    private final ChunkIngestionService ingestionService;

    public IngestController(ChunkIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

   @PostMapping("/ingest")
   public ResponseEntity<String> ingest(@RequestBody IngestRequest request) {
    String text = request.getText();
    Map<String, String> metadata = request.getMetadata();
    ingestionService.ingest(text, metadata);
    return ResponseEntity.ok("Stored");

}
}

