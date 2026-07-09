package com.sindhura.semsearch;

import java.sql.Connection;
import java.sql.DriverManager;

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
        

        try {

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to Postgres: " + conn.getMetaData().getDatabaseProductVersion());
            DBClient dbClient = new DBClient(conn);

            VectorStore store = new VectorStore(client);
            VectorPersister persister= new VectorPersister(client);
            VectorSnapshot snapshot = persister.persist();
            snapshot.getDocuments().forEach(store::add);
            snapshot.getDocuments().forEach(dbClient::insertDocument);
            CLIUtil.interactiveCLI(store);  
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
