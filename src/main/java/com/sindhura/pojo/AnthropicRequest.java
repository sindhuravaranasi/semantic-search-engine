package com.sindhura.pojo;

import java.util.List;

public class AnthropicRequest {
    String model;
    int max_tokens;
    String system;
    List<Message> messages;

    public AnthropicRequest(String model, int max_tokens, String system, List<Message> messages) {
        this.model = model;
        this.max_tokens = max_tokens;
        this.system = system;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMax_tokens() {
        return max_tokens;
    }

    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
