package com.sindhura.semsearch;

import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("VOYAGE_API_KEY");
        EmbeddingClient client = new EmbeddingClient(apiKey);

        List<String> testTexts = List.of(
            "I love my dog",
            "I adore my puppy"
        );

        try {
            List<float[]> embeddings = client.getEmbedding(testTexts, "document");
            System.out.println("Total embeddings: " + embeddings.size());
            System.out.println("For vector 1, Embedding length: " + embeddings.get(0).length);
            for(int i=0;i<5;i++) {
            System.out.print(embeddings.get(0)[i]+ " ");
            }
            System.out.println("For vector 2, Embedding length: " + embeddings.get(1).length);
            for(int i=0;i<5;i++) {
            System.out.print(embeddings.get(1)[i]+ " ");
            }
        } catch(Exception e) {
            System.err.println("Error occurred while fetching embeddings: " + e.getMessage());
            e.printStackTrace();
        }

        
    }
}
