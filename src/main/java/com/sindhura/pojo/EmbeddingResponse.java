package com.sindhura.pojo;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class EmbeddingResponse {
    private String object;
    @SerializedName("data")
    private List<EmbeddingData> embeddingData;
    private String model;
    private Usage usage;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @SerializedName("data")
    public List<EmbeddingData> getEmbeddingData() {
        return embeddingData;
    }

    public void setEmbeddingData(List<EmbeddingData> embeddingData) {
        this.embeddingData = embeddingData;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }
}