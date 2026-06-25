package com.sindhura.semsearch;

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
            CLIUtil.interactiveCLI(store);
        } catch(Exception e) {
            System.err.println("Error occurred while fetching embeddings: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
