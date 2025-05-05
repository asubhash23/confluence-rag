import os
import requests

INGEST_ENDPOINT = "http://localhost:8080/ingest"
TEXT_FILES_DIR = "./confluence-pages"

for filename in os.listdir(TEXT_FILES_DIR):
    if filename.endswith(".txt"):
        path = os.path.join(TEXT_FILES_DIR, filename)
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()

        payload = {
            "text": content,
            "metadata": {
                "source": filename.replace(".txt", "")
            }
        }

        response = requests.post(
            INGEST_ENDPOINT,
            json=payload  # ✅ sends Content-Type: application/json
        )

        print(f"{filename} → Status {response.status_code}")
