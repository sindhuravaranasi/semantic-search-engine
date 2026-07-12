package com.sindhura.chunking;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DocumentChunker {
    
    public static List<String> chunkDocument(Path filePath) {
        List<String> chunks = new ArrayList<>();
        try {
            List<String> texts = Files.readAllLines(filePath);
            String pendingHeader = null;
            StringBuilder currentBullet = null;
            for (int i = 0; i < texts.size(); i++) {
                String text = texts.get(i);
                if (text.isEmpty()) {
                    // blank line finalizes any in-progress bullet
                    if (currentBullet != null) {
                        chunks.add(currentBullet.toString().trim());
                        currentBullet = null;
                    }
                    continue;
                }
                boolean isBullet = text.charAt(0) == '•' || text.charAt(0) == '-' || text.charAt(0) == '*';
                if (isBullet) {
                    // assemble multi-line bullet: start or continue
                    String trimmed = text.trim();
                    // remove leading bullet marker and possible following space
                    String withoutMarker = trimmed.length() > 1 && (trimmed.charAt(0) == '•' || trimmed.charAt(0) == '-' || trimmed.charAt(0) == '*')
                            ? trimmed.substring(1).trim()
                            : trimmed;
                    if (currentBullet == null) {
                        if (pendingHeader != null) {
                            currentBullet = new StringBuilder(pendingHeader + " - " + withoutMarker);
                            pendingHeader = null;
                        } else {
                            currentBullet = new StringBuilder(withoutMarker);
                        }
                    } else {
                        // continue previous bullet on new line
                        chunks.add(currentBullet.toString().trim());
    currentBullet = null;
    // then start the new one
    if (pendingHeader != null) {
        currentBullet = new StringBuilder(pendingHeader + " - " + withoutMarker);
        pendingHeader = null;
    } else {
        currentBullet = new StringBuilder(withoutMarker);
    }
                    }
                } else {
                    // any non-bullet finalizes an in-progress bullet
                    if (currentBullet != null) {
                        currentBullet.append(' ').append(text.trim());
                    }
                    else {
                        int nextIndex = i + 1;
                    boolean nextIsBullet = false;
                    while (nextIndex < texts.size() && texts.get(nextIndex).isEmpty()) {
                        nextIndex++;
                    }
                    if (nextIndex < texts.size()) {
                        String nextText = texts.get(nextIndex);
                        nextIsBullet = !nextText.isEmpty() && (nextText.charAt(0) == '•' || nextText.charAt(0) == '-' || nextText.charAt(0) == '*');
                    }
                    if (nextIsBullet) {
                        pendingHeader = text.trim();
                    } else {
                        String[] sentences = text.split("(?<=[.!?])\\s+");
                        for (String sentence : sentences) {
                            if (!sentence.trim().isEmpty()) {
                                chunks.add(sentence.trim());
                            }
                        }
                    }
                }
                    
            }
        }
            if (pendingHeader != null) {
                chunks.add(pendingHeader);
            }
            if (currentBullet != null) {
                chunks.add(currentBullet.toString().trim());
            }
        } catch (Exception e) {
            System.err.println("Error occurred while reading document: " + e.getMessage());
            e.printStackTrace();
        }
        return chunks;
    }
}
