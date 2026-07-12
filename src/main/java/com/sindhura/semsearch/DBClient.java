package com.sindhura.semsearch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sindhura.pojo.Document;
import com.sindhura.pojo.ScoredDocument;

import java.sql.Connection;
import com.pgvector.PGvector;

class DBClient {

    private final Connection conn;

    public DBClient(Connection conn) throws SQLException {
        this.conn = conn;
        PGvector.addVectorType(conn);
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS vector_store (\n" +
                "    id SERIAL PRIMARY KEY,\n" +
                "    document_text TEXT UNIQUE,\n" +
                "    document_embedding VECTOR(1024)\n" +
                ");";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDocument(Document document) {
        
        String sql = "INSERT INTO vector_store (document_text,  document_embedding) VALUES (?, ?) on conflict(document_text) do nothing";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            // Set parameters for the prepared statement
            preparedStatement.setString(1, document.getText());
            PGvector embedding = new PGvector(document.getEmbedding());
            preparedStatement.setObject(2, embedding);
            // Execute the insert operation
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ScoredDocument> searchDocuments(float[] queryEmbedding, int k) {
        List<ScoredDocument> results = new ArrayList<>();
        String sql = "SELECT document_text, score\n" + //
                        "FROM (\n" + //
                        "    SELECT document_text, document_embedding <=> ? AS score\n" + //
                        "    FROM vector_store\n" + //
                        ") ranked\n" + //
                        "ORDER BY score\n" + //
                        "LIMIT ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            PGvector queryVector = new PGvector(queryEmbedding);
            preparedStatement.setObject(1, queryVector);
            preparedStatement.setInt(2, k);
            var rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String text = rs.getString("document_text");
                double score = rs.getDouble("score");
                ScoredDocument scoredDocument = new ScoredDocument(text, score);
                results.add(scoredDocument);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Document> getDocuments() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT document_text FROM vector_store";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            var rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String text = rs.getString("document_text");
                Document document = new Document(text, null);
                documents.add(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documents;
    }

    public boolean documentExists(String documentText) {
        String sql = "SELECT COUNT(*) FROM vector_store WHERE document_text = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, documentText);
            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
