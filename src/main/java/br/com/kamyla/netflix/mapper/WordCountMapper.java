package br.com.kamyla.netflix.mapper;

import br.com.kamyla.netflix.util.TextProcessor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.io.StringReader;

public class WordCountMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final IntWritable ONE = new IntWritable(1);
    private final Text wordKey = new Text();

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

            if (record.size() <= 11) {
                return;
            }

            String description = record.get(11);

            for (String word : TextProcessor.process(description)) {

                wordKey.set(word);
                context.write(wordKey, ONE);

                context.getCounter(
                        "NETFLIX",
                        "TOTAL_WORDS"
                ).increment(1);
            }

        } catch (Exception e) {
            context.getCounter(
                    "NETFLIX",
                    "INVALID_CSV_RECORDS"
            ).increment(1);
        }
    }
}
