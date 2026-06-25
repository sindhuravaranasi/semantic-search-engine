package com.sindhura.semsearch;

import java.util.List;
import java.util.Scanner;

import com.sindhura.pojo.Document;
import com.sindhura.pojo.ScoredDocument;

public class CLIUtil {

    public static void interactiveCLI(VectorStore store) {
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
                List<ScoredDocument> results = store.search(query, k);
                System.out.println("Search Results:");
                for (ScoredDocument scoredDoc : results) {
                    System.out.println("Text: " + scoredDoc.getDocument().getText() + ", Score: " + scoredDoc.getScore());
                }   
            } else if (command.equals("display")) {
                // count + first 5 documents
                List<Document> documents = store.getDocuments();
                System.out.println("Total Documents: " + documents.size());
                System.out.println("First 5 Documents:");
                for (int i = 0; i < Math.min(5, documents.size()); i++) {
                    System.out.println(documents.get(i).getText());
                }
            } else if (command.equals("exit") || command.equals("quit")) {
                break;
            } else {
                System.out.println("Unrecognized command.");
            }
        }
        scanner.close();
    } 
}
