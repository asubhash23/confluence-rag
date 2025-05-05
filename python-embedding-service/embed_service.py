from fastapi import FastAPI, Request
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
from typing import List
import uvicorn

# Load model once at startup
model = SentenceTransformer("all-MiniLM-L6-v2")

app = FastAPI()

class EmbedRequest(BaseModel):
    texts: List[str]

class EmbedResponse(BaseModel):
    embeddings: List[List[float]]

@app.post("/embed", response_model=EmbedResponse)
def embed(request: EmbedRequest):
    embeddings = model.encode(request.texts, convert_to_numpy=True).tolist()
    return {"embeddings": embeddings}

if __name__ == "__main__":
    uvicorn.run("embed_service:app", host="0.0.0.0", port=8000)
