package com.sindhura.semsearch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.sindhura.pojo.Document;
import com.sindhura.pojo.ScoredDocument;

import io.github.cdimascio.dotenv.Dotenv;

public class App 
{
    public static void main( String[] args )
    {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("VOYAGE_API_KEY");
        EmbeddingClient client = new EmbeddingClient(apiKey);


        try {

            VectorStore store = new VectorStore(client);
            index(client, store);
            List<ScoredDocument> results = store.search("I love my dog", 5);
            System.out.println("Search Results:");
            for (ScoredDocument scoredDoc : results) {
                System.out.println("Text: " + scoredDoc.getDocument().getText() + ", Score: " + scoredDoc.getScore());
            }
        } catch(Exception e) {
            System.err.println("Error occurred while fetching embeddings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void index(EmbeddingClient client, VectorStore store) {
        try {
            List<String> texts = Files.readAllLines(Path.of("data/documents.txt"));
            List<float[]> embeddings = client.getEmbedding(texts, "document");
            for (int i = 0; i < texts.size(); i++) {
                Document d = new Document(texts.get(i), embeddings.get(i));
                store.add(d); 
            }
        } catch (Exception e) {
            System.err.println("Error occurred while indexing document: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
