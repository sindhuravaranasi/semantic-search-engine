package com.sindhura.pojo;

public class EmbeddingData {
        private String object;
        private float[] embedding;
        private int index;
        private String text;

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public float[] getEmbedding() {
            return embedding;
        }

        public void setEmbedding(float[] embedding) {
            this.embedding = embedding;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
}
