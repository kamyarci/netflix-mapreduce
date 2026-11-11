
package br.com.kamyla.netflix.reducer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DurationReducer
        extends Reducer<Text, IntWritable, Text, Text> {

    private final Text result = new Text();

    @Override
    protected void reduce(
            Text key,
            Iterable<IntWritable> values,
            Context context
    ) throws IOException, InterruptedException {

        List<Integer> durations = new ArrayList<>();
        Map<Integer, Integer> frequencies = new HashMap<>();

        long sum = 0;

        for (IntWritable value : values) {
            int duration = value.get();

            durations.add(duration);
            sum += duration;

            frequencies.merge(duration, 1, Integer::sum);
        }

        if (durations.isEmpty()) {
            return;
        }

        Collections.sort(durations);

        int count = durations.size();
        double mean = (double) sum / count;

        double median;

        if (count % 2 == 0) {
            median = (
                    (long) durations.get(count / 2 - 1)
                            + durations.get(count / 2)
            ) / 2.0;
        } else {
            median = durations.get(count / 2);
        }

        int maxFrequency = Collections.max(frequencies.values());

        List<Integer> modes = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : frequencies.entrySet()) {
            if (entry.getValue() == maxFrequency) {
                modes.add(entry.getKey());
            }
        }

        Collections.sort(modes);

        int min = durations.get(0);
        int max = durations.get(count - 1);

        String unit = key.toString().equals("SERIE")
                ? "temporadas"
                : "minutos";

        result.set(String.format(
                java.util.Locale.US,
                "quantidade=%d | media=%.2f | mediana=%.2f | moda=%s"
                        + " | frequencia_moda=%d | minimo=%d"
                        + " | maximo=%d | unidade=%s",
                count,
                mean,
                median,
                modes,
                maxFrequency,
                min,
                max,
                unit
        ));

        context.write(key, result);
    }
}
