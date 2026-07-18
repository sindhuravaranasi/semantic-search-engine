package com.sindhura.semsearch;

import com.google.gson.Gson;
import com.sindhura.pojo.EmbeddingData;
import com.sindhura.pojo.EmbeddingRequest;
import com.sindhura.pojo.EmbeddingResponse;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.ArrayList;
import java.util.List;

public class EmbeddingClient {

    private final String apiKey;
    private final OkHttpClient httpClient;
    private final Gson gson;

    public EmbeddingClient(String apiKey) {
        // Initialize the client with the provided API key
       this.apiKey = apiKey;
       this.httpClient = new OkHttpClient();
       this.gson = new Gson();
    }

    public List<float[]> getEmbedding(List<String> input, String inputType) {
        // Implement the logic to send a request to the embedding service and retrieve the embedding for the given text
        // Use the apiKey for authentication
        // Use httpClient to make HTTP requests
        // Use gson to parse the response JSON into EmbeddingResponse object
        // Return the embedding vector as a List<Float>
        List<float[]> embeddings = new ArrayList<>();
        EmbeddingRequest requestBody = new EmbeddingRequest(input, "voyage-3.5", inputType);

        Request request = new Request.Builder()
                .url("https://api.voyageai.com/v1/embeddings")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.parse("application/json")))
                .build();

        try (var response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                 System.err.println("Voyage API error: " + response.code() + " - " + response.message());
                throw new RuntimeException("Voyage API returned " + response.code());
        }
            String responseBody = response.body().string();
            EmbeddingResponse embeddingResponse = gson.fromJson(responseBody, EmbeddingResponse.class);
            for(EmbeddingData data : embeddingResponse.getEmbeddingData()) {
                embeddings.add(data.getEmbedding());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching embedding", e);
        }
        return embeddings;
    }
    
}