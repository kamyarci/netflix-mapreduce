package br.com.kamyla.netflix;

import br.com.kamyla.netflix.util.TextNormalizer;
import br.com.kamyla.netflix.util.TextProcessor;

public class TestNormalization {

    public static void main(String[] args) {

        String text =
                "The world's story happened after 1000 missions with a B-17 aircraft.";

        System.out.println("Original:");
        System.out.println(text);

        System.out.println("\nNormalizado:");
        System.out.println(TextNormalizer.normalize(text));

        System.out.println("\nPalavras consideradas:");

        for (String word : TextProcessor.process(text)) {
            System.out.println(word);
        }
    }
}
