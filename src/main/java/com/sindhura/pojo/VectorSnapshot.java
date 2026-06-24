package com.sindhura.pojo;

import java.util.List;

public class VectorSnapshot {
    private List<Document> documents;
    private String fileHash;

    public VectorSnapshot(List<Document> documents, String fileHash) {
        this.documents = documents;
        this.fileHash = fileHash;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
}
