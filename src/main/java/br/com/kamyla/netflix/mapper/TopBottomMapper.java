package br.com.kamyla.netflix.mapper;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class TopBottomMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text word = new Text();
    private final IntWritable frequency = new IntWritable();

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context
    ) throws IOException, InterruptedException {

        String line = value.toString();

        String[] parts = line.split("\\t");

        if (parts.length != 2) {
            return;
        }

        try {
            word.set(parts[0]);
            frequency.set(Integer.parseInt(parts[1]));

            context.write(word, frequency);

        } catch (NumberFormatException e) {
            context.getCounter(
                    "NETFLIX",
                    "INVALID_WORD_COUNT_RECORDS"
            ).increment(1);
        }
    }
}
