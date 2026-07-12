package com.sindhura.semsearch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.stream.Collectors;

import com.sindhura.chunking.DocumentChunker;
import com.sindhura.pojo.Document;

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

        
        
        try {

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to Postgres: " + conn.getMetaData().getDatabaseProductVersion());
            DBClient dbClient = new DBClient(conn);

            List<String> texts = Files.readAllLines(Path.of("data/documents.txt"));
            List<String> newText = texts.stream()
                .filter(text -> !dbClient.documentExists(text))
                .collect(Collectors.toList());

            if(!newText.isEmpty()) {
                System.out.println("New documents to be inserted to DB: " + newText.size());
                List<float[]> embeddings = client.getEmbedding(newText, "document");
                for(int i=0;i<newText.size();i++) {
                    String text = newText.get(i);
                    float[] embeddingList = embeddings.get(i);
                    dbClient.insertDocument(new Document(text, embeddingList));
                }
            }
            
            List<String> chunks = DocumentChunker.chunkDocument(Path.of("data/resume.txt"));

            List<String> newChunks = chunks.stream()
                .filter(chunk -> !dbClient.documentExists(chunk))
                .collect(Collectors.toList());

            if(!newChunks.isEmpty()) {
                System.out.println("New chunks to be inserted to DB: " + newChunks.size());
                List<float[]> resumeEmbeddings = client.getEmbedding(newChunks, "document");
                for(int i=0;i<newChunks.size();i++) {
                    String chunk = newChunks.get(i);
                    float[] embeddingList = resumeEmbeddings.get(i);
                    dbClient.insertDocument(new Document(chunk, embeddingList));
                }
            }
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
