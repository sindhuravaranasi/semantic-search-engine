package com.sindhura.semsearch;

import java.util.ArrayList;
import java.util.List;

import com.sindhura.pojo.Document;
import com.sindhura.pojo.ScoredDocument;

public class VectorStore {
    private List<Document> documents;
    private EmbeddingClient client;


    public VectorStore(EmbeddingClient client) {
        this.documents = new ArrayList<>();
        this.client = client;
    }

    public void add(Document document) {
       documents.add(document);
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public List<ScoredDocument> search(String query, int topK) {
        List<ScoredDocument> scoredDocuments = new ArrayList<>();
        List<float[]> embeddings = client.getEmbedding(List.of(query), "query");
        float[] queryEmbedding = embeddings.get(0);

        for (Document document : documents) {
            double score = VectorMath.cosineSimilarity(queryEmbedding, document.getEmbedding());
            scoredDocuments.add(new ScoredDocument(document.getText(), score));
        }

        scoredDocuments.sort((d1, d2) -> Double.compare(d2.getScore(), d1.getScore()));

        return scoredDocuments.subList(0, Math.min(topK, scoredDocuments.size()));
    }
}
