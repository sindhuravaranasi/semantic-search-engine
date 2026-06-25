# Semantic Search Engine

A from-scratch semantic search engine built in Java — no frameworks, no vector DB libraries. 
Built to understand how retrieval-based search actually works under the hood, the same primitive 
that powers RAG systems.

## What it does (so far)
- Calls the Voyage AI embeddings API directly via OkHttp
- Parses responses with Gson into clean POJOs
- Hand-written cosine similarity (dot product, magnitude — no math libraries)
- In-memory vector store with top-k retrieval
- JSON persistence
- CLI for indexing documents and running searches

## Setup
1. Clone the repo
2. Create a `.env` file in the project root: VOYAGE_API_KEY=<your_key_here>
3. Run `Main.java`

## Tech
Java, OkHttp, Gson, Voyage AI embeddings API (`voyage-3.5`)

## Why no frameworks?
The point of this project is to understand retrieval mechanics — embeddings, cosine similarity, 
vector search — by implementing them directly, rather than calling a library that hides the math.
