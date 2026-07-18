package com.sindhura.semsearch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.stream.Collectors;

import com.sindhura.chunking.ChunkingUtil;
import com.sindhura.chunking.DocumentChunker;
import com.sindhura.pojo.Document;
import com.sindhura.pojo.Message;

import io.github.cdimascio.dotenv.Dotenv;

public class App 
{
    public static void main( String[] args )
    {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("VOYAGE_API_KEY");
        

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");
        Connection conn = null;

        String anthropicKey = dotenv.get("ANTHROPIC_API_KEY");
       
        try {
            EmbeddingClient client = new EmbeddingClient(apiKey);
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to Postgres: " + conn.getMetaData().getDatabaseProductVersion());
            DBClient dbClient = new DBClient(conn);
            AnthropicClient anthropicClient = new AnthropicClient(anthropicKey);
            ChunkingUtil.insertNewChunks(dbClient, client, Path.of("data/resume.txt"));
            ChunkingUtil.insertNewChunks(dbClient, client, Path.of("data/documents.txt"));

            CLIUtil.interactiveCLI(dbClient, client, anthropicClient);  
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
