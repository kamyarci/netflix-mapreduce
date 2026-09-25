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

public class DescriptionLengthMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text titleKey = new Text();
    private final IntWritable wordCountValue = new IntWritable();

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

            String title = record.get(2);
            String description = record.get(11);

            int wordCount =
                    TextProcessor.process(description).size();

            titleKey.set(title);
            wordCountValue.set(wordCount);

            context.write(titleKey, wordCountValue);

        } catch (Exception e) {
            context.getCounter(
                    "NETFLIX",
                    "INVALID_DESCRIPTION_RECORDS"
            ).increment(1);
        }
    }
}