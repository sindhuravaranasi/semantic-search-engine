package com.sindhura.semsearch;

import java.util.List;

import com.google.gson.Gson;
import com.sindhura.pojo.AnthropicRequest;
import com.sindhura.pojo.AnthropicResponse;
import com.sindhura.pojo.Message;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AnthropicClient {

    private final String apiKey;
    private final OkHttpClient httpClient;
    private final Gson gson;

    public AnthropicClient(String apiKey) {
        // Initialize the client with the provided API key
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    public String getSummary(String system, List<Message> messages) {
        // Implement the logic to send a request to the Anthropic service and retrieve the summary for the given messages
        // Use the apiKey for authentication
        // Use httpClient to make HTTP requests
        // Use gson to parse the response JSON into appropriate object
        // Return the summary as a String

        AnthropicRequest requestBody = new AnthropicRequest("claude-sonnet-4-6",1024, system, messages);
        Request request = new Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("anthropic-version", "2023-06-01")
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.parse("application/json")))
                .build();

        try (var response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Anthropic API error: " + response.code() + " - " + response.message());
                throw new RuntimeException("Anthropic API returned " + response.code());
            }
            String responseBody = response.body().string();
            AnthropicResponse anthropicResponse = gson.fromJson(responseBody, AnthropicResponse.class);
            return anthropicResponse.getContent().get(0).getText();
        } catch(Exception e) {
            throw new RuntimeException("Error occurred while fetching summary", e);
        }
    }
}
