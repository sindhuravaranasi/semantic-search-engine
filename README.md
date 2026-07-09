# Semantic Search Engine

A from-scratch semantic search engine built in Java — no frameworks, no vector DB libraries for Week 1. Week 2 integrates pgvector (Postgres) for production-grade vector storage and retrieval, replacing the hand-rolled in-memory store.
Built to understand how retrieval-based search actually works under the hood, the same primitive 
that powers RAG systems.

## What it does (so far)
## Week 1- In-memory semantic search (complete)
- Calls the Voyage AI embeddings API directly via OkHttp
- Parses responses with Gson into clean POJOs
- Hand-written cosine similarity (dot product, magnitude — no math libraries)
- In-memory vector store with top-k retrieval
- JSON persistence
- Interactive CLI with `search`, `display`, and `exit` commands

## Week 2- pgvector + chunking (in progress)
- PostgreSQL 17 + pgvector 0.8.4 as the vector storage and search layer
- Schema: `vector_store` table with `id`, `document_text`, `text`, `document_embedding vector(1024)`
- JDBC-driven document insertion with `PGvector` type bridging
- `<=>` cosine distance operator replacing hand-written cosine similarity
- HNSW index for approximate nearest neighbor search (coming — Day 3)
- Chunking logic for long documents (coming — Day 4)
- CLI wired to pgvector pipeline (coming — Day 5)
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


## Tech
Java, OkHttp, Gson, Voyage AI embeddings API (`voyage-3.5`)

## Why no frameworks (Week 1)?
The point of Week 1 was to understand retrieval mechanics — embeddings, cosine 
similarity, vector search — by implementing them directly, rather than calling a 
library that hides the math. Week 2 deliberately introduces pgvector to understand 
*why* a production vector store is needed (O(n·d) brute force doesn't scale) and 
*how* HNSW indexing works, grounded in the Week 1 fundamentals.
