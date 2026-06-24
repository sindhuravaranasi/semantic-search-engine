package com.sindhura.semsearch;

import java.util.List;

import com.sindhura.pojo.ScoredDocument;
import com.sindhura.pojo.VectorSnapshot;

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
            VectorPersister persister= new VectorPersister(client);
            VectorSnapshot snapshot = persister.persist();
            snapshot.getDocuments().forEach(store::add);
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
}
