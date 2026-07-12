package com.sindhura.semsearch;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import com.sindhura.chunking.DocumentChunker;
import com.sindhura.pojo.Document;
import com.sindhura.pojo.VectorSnapshot;

import io.github.cdimascio.dotenv.Dotenv;

public class App 
{
    public static void main( String[] args )
    {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("VOYAGE_API_KEY");
        EmbeddingClient client = new EmbeddingClient(apiKey);

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");
        Connection conn = null;

        List<String> chunks = DocumentChunker.chunkDocument(Path.of("data/resume.txt"));
        System.out.println("Total chunks: " + chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("Chunk " + (i+1) + ": " + chunks.get(i));
            System.out.println("---");
        }
        

        try {

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to Postgres: " + conn.getMetaData().getDatabaseProductVersion());
            DBClient dbClient = new DBClient(conn);

            VectorPersister persister= new VectorPersister(client);
            VectorSnapshot snapshot = persister.persist();
            snapshot.getDocuments().forEach(dbClient::insertDocument);
            
            List<float[]> resumeEmbeddings = client.getEmbedding(chunks, "document");
            for(int i=0;i<chunks.size();i++) {
                String chunk = chunks.get(i);
                float[] embeddingList = resumeEmbeddings.get(i);
                dbClient.insertDocument(new Document(chunk, embeddingList));
            }
            System.out.println("Resume chunks inserted into DB");
        
            CLIUtil.interactiveCLI(dbClient, client);  
        } catch(Exception e) {
            System.err.println("Error occurred while fetching embeddings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the database connection
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("Database connection closed.");
                }
            } catch (Exception e) {
                System.err.println("Error occurred while closing the database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
    }
}
