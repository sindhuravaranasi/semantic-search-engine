package com.sindhura.pojo;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EmbeddingRequest {
    private List<String> input;
    private String model;

    public EmbeddingRequest(List<String> input, String model, String inputType) {
        this.input = input;
        this.model = model;
        this.inputType = inputType;
    }

    @SerializedName("input_type")
    private String inputType;

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }      
}