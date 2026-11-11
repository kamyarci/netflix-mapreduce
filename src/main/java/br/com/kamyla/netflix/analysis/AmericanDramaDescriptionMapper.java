
package br.com.kamyla.netflix.analysis;

import br.com.kamyla.netflix.util.TextProcessor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

public class AmericanDramaDescriptionMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final IntWritable ONE = new IntWritable(1);
    private final Text outputKey = new Text();

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context
    ) throws IOException, InterruptedException {

        String line = value.toString();

        if (line.startsWith("show_id,type,title,")) {
            return;
        }

        try {
            CSVRecord record = CSVFormat.DEFAULT
                    .parse(new StringReader(line))
                    .getRecords()
                    .get(0);

            if (record.size() != 12) {
                context.getCounter("NETFLIX", "INVALID_CSV_RECORDS")
                        .increment(1);
                return;
            }

            boolean isMovie = record.get(1).trim().equals("Movie");

            boolean isAmerican = Arrays.stream(record.get(5).split(","))
                    .map(String::trim)
                    .anyMatch("United States"::equals);

            boolean isDrama = Arrays.stream(record.get(10).split(","))
                    .map(String::trim)
                    .anyMatch("Dramas"::equals);

            if (!isMovie || !isAmerican || !isDrama) {
                return;
            }

            String description = record.get(11);

            for (String word : TextProcessor.process(description)) {
                outputKey.set(word);
                context.write(outputKey, ONE);
            }

        } catch (Exception e) {
            context.getCounter("NETFLIX", "INVALID_CSV_RECORDS")
                    .increment(1);
        }
    }
}
