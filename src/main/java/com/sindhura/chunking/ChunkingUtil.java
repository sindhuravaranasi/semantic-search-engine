package com.sindhura.chunking;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.sindhura.pojo.Document;
import com.sindhura.semsearch.DBClient;
import com.sindhura.semsearch.EmbeddingClient;

public class ChunkingUtil {

    public static void insertNewChunks(DBClient dbClient, EmbeddingClient client, Path documentPath){
        List<String> chunks = DocumentChunker.chunkDocument(documentPath);

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
    }

    public static String promptBuilder(List<String> chunks, String question) {
        StringBuilder contentBuilder = new StringBuilder("Context:\n");
        for (int i = 0; i < chunks.size(); i++) {
            contentBuilder
                .append("[")
                .append(i + 1)
                .append("] ")
                .append(chunks.get(i))
                .append("\n");
        }
        contentBuilder.append("\nQuestion: ").append(question);
        return contentBuilder.toString();
    }
}
