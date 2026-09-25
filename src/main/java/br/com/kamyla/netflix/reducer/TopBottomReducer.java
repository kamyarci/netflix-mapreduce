package br.com.kamyla.netflix.reducer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopBottomReducer
        extends Reducer<Text, IntWritable, Text, IntWritable> {

    private final List<WordFrequency> words = new ArrayList<>();
    private long totalWords = 0;

    @Override
    protected void reduce(
            Text key,
            Iterable<IntWritable> values,
            Context context
    ) {

        for (IntWritable value : values) {
            words.add(
                    new WordFrequency(
                            key.toString(),
                            value.get()
                    )
            );
            totalWords += value.get();
        }
    }

    @Override
    protected void cleanup(Context context)
            throws IOException, InterruptedException {

        List<WordFrequency> mostFrequent = words.stream()
                .sorted(
                        Comparator
                                .comparingInt(WordFrequency::frequency)
                                .reversed()
                                .thenComparing(WordFrequency::word)
                )
                .limit(5)
                .toList();

        List<WordFrequency> leastFrequent = words.stream()
                .sorted(
                        Comparator
                                .comparingInt(WordFrequency::frequency)
                                .thenComparing(WordFrequency::word)
                )
                .limit(5)
                .toList();

        for (WordFrequency item : mostFrequent) {
            context.write(
                    new Text("MAIS_REPETIDA: " + item.word()),
                    new IntWritable(item.frequency())
            );
        }

        for (WordFrequency item : leastFrequent) {
            context.write(
                    new Text("MENOS_REPETIDA: " + item.word()),
                    new IntWritable(item.frequency())
            );
        }

        context.write(
                new Text("TOTAL_PALAVRAS:"),
                new IntWritable((int) totalWords)
        );
    }

    private record WordFrequency(
            String word,
            int frequency
    ) {
    }
}
