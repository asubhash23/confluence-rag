
# Confluence Chatbot POC

This repository contains a complete Proof of Concept (POC) for building a Retrieval-Augmented Generation (RAG) chatbot that answers questions using content from Confluence pages. The system is composed of five modular components:

## 🔧 Project Structure

```
├── ingest-script             # Python script to read .txt files and ingest to MongoDB vector store via Spring /inject endpoint
├── react-frontend            # React app for user interaction
├── spring-backend            # Spring Boot app for ingestion, vector search, and LLM interface
├── python-embedding-service  # FastAPI service to generate text embeddings
└── Ollama            		  # Running mistral LLM using Ollama 
```


---

## 1. 📄 Ingest Script (`ingest-script`)

A Python script that:
- Reads `.txt` files exported or copied from Confluence pages
- Sends the content to the Spring Boot backend’s `/ingest` endpoint, which then persists the text along with the corresponding vectors embeddings to MongoDB 

### Example Usage
```bash
cd ingest-script
python ingest.py
```

---

## 2. 🖥️ React Frontend (`react-frontend`)

A Vite + React-based interface that allows users to:
- Input questions
- Receive streaming answers

### Tech Stack
- React 18
- Vite
- Tailwind
- Fetch + SSE for streaming

### Commands
```bash
cd react-frontend
npm install
npm run dev
```

---

## 3. ☕ Spring Boot Backend (`spring-backend`)

A WebFlux-based backend that:
- Accepts raw Confluence content via `/ingest`
- Chunks and embeds text using the embedding service
- Stores embedded chunks in MongoDB Atlas with vector index
- Performs similarity search on queries
- Sends context to LLM (Ollama) and returns answers

### Endpoints
- `POST /ingest` - Accepts `{ text, metadata }` JSON,  endpoint used by python script to ingest confluence data to MongoDB vector store
- `GET /chat/stream?query=...` - Streaming LLM response via `text/event-stream`, endpoint used by react-frontend to get response to user query 
- `GET /search?query=...` - endpoint to perform similarity search 

### Config
Update your `application.properties`:
```properties
app.embedding-service.url=http://localhost:8000
spring.data.mongodb.uri=...
spring.data.mongodb.database=...
azure.ollama.url=http://localhost:11434 
```

### Build and Run
```bash
cd spring-backend
./mvnw spring-boot:run
```

---

## 4. 🧠 Embedding Service (`python-embedding-service`)

A lightweight FastAPI service that:
- Accepts plain text
- Uses `sentence-transformers` to compute embeddings
- Returns vector to Spring backend

### Tech Stack
- FastAPI
- sentence-transformers
- Uvicorn

### Run
```bash
cd python-embedding-service
pip install -r requirements.txt
uvicorn embed_service:app --host 0.0.0.0 --port 8000 --reload
```
---

## 5. 🧠 Ollama 

Framework to build and run LLMs.


### Run
```bash
curl -fsSL https://ollama.com/install.sh | sh
ollama pull mistral
nohup env OLLAMA_HOST=0.0.0.0 ollama serve > ~/ollama.log 2>&1 &
```
---