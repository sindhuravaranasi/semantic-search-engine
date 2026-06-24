package com.sindhura.semsearch;

import com.google.gson.Gson;
import com.sindhura.pojo.Document;
import com.sindhura.pojo.VectorSnapshot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class VectorPersister {

    private final Gson gson;
    private EmbeddingClient client;
    private VectorStore store;

    public VectorPersister(EmbeddingClient client) {
        this.gson = new Gson();
        this.client= client;
        this.store = new VectorStore(client);
    }

    public VectorSnapshot persist() {
        try {
            List<String> texts = Files.readAllLines(Path.of("data/documents.txt"));
            String md5 = computeMD5Hash(texts);

            Path documentsPath = Paths.get("data/documents.json");
            if (Files.exists(documentsPath)) {
                 String json = Files.readString(documentsPath);
                 VectorSnapshot vectorSnapshot = gson.fromJson(json, VectorSnapshot.class);
                 String fileHash = vectorSnapshot.getFileHash();
                 if(!md5.equals(fileHash)) {
                     index();
                     return saveSnapshot(store, md5);
                 } else {
                     System.out.println("File has not changed. No need to re-index.");
                     return vectorSnapshot;
                 }
            } else {
                index();
                return saveSnapshot(store, md5);
            }

        } catch(Exception e) {
            System.err.println("Error occurred while indexing document: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private VectorSnapshot saveSnapshot(VectorStore store, String fileHash) throws java.io.IOException {
        Path documentsPath = Paths.get("data/documents.json");
        VectorSnapshot snapshot = new VectorSnapshot(store.getDocuments(), fileHash);   
        String json = gson.toJson(snapshot);
        Files.writeString(documentsPath, json);
        return gson.fromJson(json, VectorSnapshot.class);
    }
    
    public String computeMD5Hash(List<String> texts) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (String text : texts) {
                md.update(text.getBytes());
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public void index() {
        try {
            List<String> texts = Files.readAllLines(Path.of("data/documents.txt"));
            List<float[]> embeddings = client.getEmbedding(texts, "document");
            for (int i = 0; i < texts.size(); i++) {
                Document d = new Document(texts.get(i), embeddings.get(i));
                store.add(d); 
            }
        } catch (Exception e) {
            System.err.println("Error occurred while indexing document: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
