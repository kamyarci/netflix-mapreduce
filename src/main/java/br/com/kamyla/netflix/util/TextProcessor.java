
package br.com.kamyla.netflix.util;

import java.util.Arrays;
import java.util.List;

public final class TextProcessor {

    private TextProcessor() {
    }

    public static List<String> process(String text) {

        String normalized = TextNormalizer.normalize(text);

        if (normalized.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(normalized.split("\\s+"))
                .filter(word -> word.matches("\\p{L}+"))
                .filter(word -> !StopWords.contains(word))
                .toList();
    }
}
