package br.com.kamyla.netflix.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextNormalizer {

    private static final Pattern ACRONYM =
            Pattern.compile("(?<![\\p{L}\\p{N}])(?:\\p{L}\\.){2,}");

    public static String normalize(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String normalized = text.toLowerCase(Locale.ROOT)
                .replace("'", "")
                .replace("’", "")
                .replace("´", "");

        Matcher matcher = ACRONYM.matcher(normalized);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String acronym = matcher.group().replace(".", "");
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(acronym));
        }

        matcher.appendTail(buffer);

        return buffer.toString()
                .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}