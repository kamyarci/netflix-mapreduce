package br.com.kamyla.netflix.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public final class StopWords {

    private static final Set<String> WORDS = loadStopWords();

    private StopWords() {
    }

    private static Set<String> loadStopWords() {
        Set<String> words = new HashSet<>();

        InputStream inputStream =
                StopWords.class.getClassLoader().getResourceAsStream("stopwords.txt");

        if (inputStream == null) {
            throw new IllegalStateException("Arquivo stopwords.txt não encontrado.");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();

                if (!word.isEmpty()) {
                    words.add(word);
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao carregar o arquivo de stopwords.", e
            );
        }

        return words;
    }

    public static boolean contains(String word) {
        return WORDS.contains(word);
    }
}
