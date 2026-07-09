package com.sindhura.semsearch;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.sindhura.pojo.Document;
import java.sql.Connection;
import com.pgvector.PGvector;

class DBClient {

    private final Connection conn;

    public DBClient(Connection conn) throws SQLException {
        this.conn = conn;
        PGvector.addVectorType(conn);
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
}
