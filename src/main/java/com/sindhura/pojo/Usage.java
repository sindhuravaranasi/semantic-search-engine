package com.sindhura.pojo;

import com.google.gson.annotations.SerializedName;

public class Usage {

        @SerializedName("total_tokens")
        private int totalTokens;

        public int getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(int totalTokens) {
            this.totalTokens = totalTokens;
        }
}