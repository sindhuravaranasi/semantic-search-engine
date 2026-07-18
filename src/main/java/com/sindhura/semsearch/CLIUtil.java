package com.sindhura.semsearch;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import com.sindhura.chunking.ChunkingUtil;
import com.sindhura.pojo.Document;
import com.sindhura.pojo.Message;
import com.sindhura.pojo.ScoredDocument;

public class CLIUtil {

    public static void interactiveCLI(DBClient dbClient, EmbeddingClient client, AnthropicClient anthropicClient) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String command = input.split(" ")[0];

            if (command.equals("search")) {
                // parse query + k, call store.search(...), print results
                int firstQuote=input.indexOf("\"");
                int lastQuote = input.lastIndexOf("\"");
                String query = input.substring(firstQuote+1,lastQuote);
                int k= Integer.parseInt(input.substring(lastQuote + 1).trim());
                List<float[]> embeddings = client.getEmbedding(List.of(query), "query");
                float[] queryEmbedding = embeddings.get(0);
                List<ScoredDocument> results = dbClient.searchDocuments(queryEmbedding, k);
                System.out.println("Search Results:");
                for (ScoredDocument scoredDoc : results) {
                    System.out.println("Text: " + scoredDoc.getText() + ", Score: " + (1-scoredDoc.getScore()));
                }   
            } else if (command.equals("display")) {
                // count + first 5 documents
                List<Document> documents = dbClient.getDocuments();
                System.out.println("Total Documents: " + documents.size());
                System.out.println("First 5 Documents:");
                for (int i = 0; i < Math.min(5, documents.size()); i++) {
                    System.out.println(documents.get(i).getText());
                }
            } else if(command.equals("ask") ) {
                int firstQuote=input.indexOf("\"");
                int lastQuote = input.lastIndexOf("\"");
                String query = input.substring(firstQuote+1,lastQuote);
                List<float[]> embeddings = client.getEmbedding(List.of(query), "query");
                float[] queryEmbedding = embeddings.get(0);
                int k=5; // number of top chunks to retrieve
                List<ScoredDocument> results = dbClient.searchDocuments(queryEmbedding, k);
                List<String> resumeChunks = results.stream()
                    .map(ScoredDocument::getText)
                    .toList();
                String system = "You are a helpful assistant answering questions about a candidate's background. Answer only using the context provided below. If the answer is not in the context, say so.";
                String content = ChunkingUtil.promptBuilder(resumeChunks, query);

                List<Message> messages = List.of(new Message("user", content));
                String summary = anthropicClient.getSummary(system, messages);
                System.out.println("Summary of the candidate's background: " + summary);

            } else if (command.equals("exit") || command.equals("quit")) {
                break;
            } else {
                System.out.println("Unrecognized command.");
            }
        }
        scanner.close();
    } 
}
