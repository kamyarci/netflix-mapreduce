package br.com.kamyla.netflix.reducer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class DescriptionLengthReducer
        extends Reducer<Text, IntWritable, Text, IntWritable> {

    private String longestTitle = null;
    private int longestCount = Integer.MIN_VALUE;

    private String shortestTitle = null;
    private int shortestCount = Integer.MAX_VALUE;

    @Override
    protected void reduce(
            Text key,
            Iterable<IntWritable> values,
            Context context
    ) {

        for (IntWritable value : values) {

            int count = value.get();
            String title = key.toString();

            if (count > longestCount
                    || (count == longestCount
                    && title.compareTo(longestTitle) < 0)) {

                longestCount = count;
                longestTitle = title;
            }

            if (count < shortestCount
                    || (count == shortestCount
                    && title.compareTo(shortestTitle) < 0)) {

                shortestCount = count;
                shortestTitle = title;
            }
        }
    }

    @Override
    protected void cleanup(Context context)
            throws IOException, InterruptedException {

        if (longestTitle != null) {
            context.write(
                    new Text("MAIS_LONGA: " + longestTitle),
                    new IntWritable(longestCount)
            );
        }

        if (shortestTitle != null) {
            context.write(
                    new Text("MAIS_CURTA: " + shortestTitle),
                    new IntWritable(shortestCount)
            );
        }
    }
}
