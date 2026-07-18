package com.sindhura.pojo;

import java.util.List;

public class AnthropicResponse {
    List<ContentBlock> content;
    String stop_reason;

    public AnthropicResponse(List<ContentBlock> content, String stop_reason) {
        this.content = content;
        this.stop_reason = stop_reason;
    }

    public List<ContentBlock> getContent() {
        return content;
    }

    public void setContent(List<ContentBlock> content) {
        this.content = content;
    }

    public String getStop_reason() {
        return stop_reason;
    }

    public void setStop_reason(String stop_reason) {
        this.stop_reason = stop_reason;
    }
}
