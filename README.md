# Semantic Search Engine

A from-scratch semantic search engine built in Java — no frameworks, no vector DB libraries for Week 1. Week 2 integrates pgvector (Postgres) for production-grade vector storage and retrieval, replacing the hand-rolled in-memory store.
Built to understand how retrieval-based search actually works under the hood, the same primitive 
that powers RAG systems.

## What it does (so far)
## Week 1- In-memory semantic search (complete)
- Calls the Voyage AI embeddings API directly via OkHttp
- Parses responses with Gson into clean POJOs
- Hand-written cosine similarity (dot product, magnitude — no math libraries)
- In-memory vector store with top-k retrieval (O(n·d))
- JSON persistence with MD5-based change detection
- Interactive CLI with `search`, `display`, and `exit` commands

## Week 2- pgvector + chunking (complete)
- PostgreSQL 17 + pgvector 0.8.4 as the vector storage and search layer
- Schema: `vector_store` table with `id SERIAL`, `document_text TEXT UNIQUE`, `text`, `document_embedding vector(1024)`
- JDBC-driven document insertion with `PGvector` type bridging; `ON CONFLICT DO NOTHING` for idempotent inserts
- `<=>` cosine distance operator replacing hand-written cosine similarity
- HNSW index with `vector_cosine_ops` for approximate nearest neighbor search
- DB-native "skip if already indexed" check (`documentExists()`) — no redundant Voyage API calls
- `DocumentChunker`: newline-based chunking with header merging and multi-line bullet assembly
- Unified corpus: 21 test sentences + 41 resume chunks in one `vector_store` table
- `VectorMath` retained as reference implementation showing the math `<=>` performs internally
- CLI wired to pgvector pipeline

### Week 3 — LLM generation / full RAG (complete ✅)
- `AnthropicClient`: raw OkHttp calls to `api.anthropic.com/v1/messages` — no SDK
- `AnthropicRequest`/`AnthropicResponse`/`ContentBlock` POJOs matching confirmed response shape
- `ask "<query>"` CLI command: embed query → retrieve top-5 chunks via pgvector → build numbered context prompt → call Claude → return synthesized answer
- System prompt grounds Claude to retrieved context only — verified: correct answers, correct "not in context" responses, no hallucination
- `ChunkingUtil.insertNewChunks()` handles both `documents.txt` and `resume.txt` at startup
- `ANTHROPIC_API_KEY` added to `.env`
-----

## Setup
1. Clone the repo
2. Create a `.env` file in the project root: VOYAGE_API_KEY=<your_key_here>
3. Run `Main.java`

### Prerequisites
- Java 17+
- Maven
- PostgreSQL 17 with pgvector extension
- A Voyage AI API key

### Postgres setup
```bash
brew install postgresql@17
brew services start postgresql@17
psql postgres
```
Then inside psql:
```sql
CREATE DATABASE semantic_search;
\c semantic_search
CREATE EXTENSION vector;
```
----

### Source data
- `data/documents.txt` — one sentence per line, indexed on first run
- `data/resume.txt` — plain text resume, chunked and indexed on first run
- Both files are re-read on every startup; only new content triggers a Voyage API call

---

### CLI commands
- `search "<query>" <k>` — semantic search, returns top-k results with similarity scores
- `ask "<query>"` — RAG: retrieves top-5 chunks, generates synthesized answer via Claude
- `display` — shows total document count and first 5 indexed documents
- `exit` / `quit` — ends the session

---


## Tech stack
- Java 17, Maven
- OkHttp 4.12.0 — HTTP client for Voyage API
- Gson 2.10.1 — JSON serialization
- PostgreSQL 17 + pgvector 0.8.4 — vector storage and HNSW search
- Voyage AI `voyage-3.5` — 1024-dimensional embeddings
- PostgreSQL JDBC driver 42.7.3
- pgvector Java client 0.1.6
- java-dotenv 5.2.2 — `.env` file loading

---

## Why no frameworks (Week 1)?
The point of Week 1 was to understand retrieval mechanics — embeddings, cosine 
similarity, vector search — by implementing them directly, rather than calling a 
library that hides the math. Week 2 deliberately introduces pgvector to understand 
*why* a production vector store is needed (O(n·d) brute force doesn't scale) and 
*how* HNSW indexing works, grounded in the Week 1 fundamentals.
