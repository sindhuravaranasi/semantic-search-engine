package com.sindhura.semsearch;

public class VectorMath {
    
    public static double dotProduct(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }
        double dotProduct = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
        }
        return dotProduct;
    }

    public static double magnitude(float[] vector) {
        double sum = 0.0;
        for (float v : vector) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    public static double cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }
        double magnitudeA = magnitude(vectorA);
        double magnitudeB = magnitude(vectorB);
        if (magnitudeA == 0 || magnitudeB == 0) {
            return 0.0; // Handle the case where one or both vectors are zero vectors
        }
        return dotProduct(vectorA, vectorB) / (magnitudeA * magnitudeB);
    }
}
