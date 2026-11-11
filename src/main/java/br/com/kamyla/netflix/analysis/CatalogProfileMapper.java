
package br.com.kamyla.netflix.analysis;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.io.StringReader;

public class CatalogProfileMapper
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
                context.getCounter(
                        "NETFLIX", "INVALID_CSV_RECORDS"
                ).increment(1);
                return;
            }

            emitValues("DIRETOR", record.get(3), context);
            emitValues("ATOR", record.get(4), context);
            emitValues("PAIS", record.get(5), context);

        } catch (Exception e) {
            context.getCounter(
                    "NETFLIX", "INVALID_CSV_RECORDS"
            ).increment(1);
        }
    }

    private void emitValues(
            String category,
            String field,
            Context context
    ) throws IOException, InterruptedException {

        if (field == null || field.isBlank()) {
            context.getCounter(
                    "NETFLIX", "MISSING_" + category
            ).increment(1);
            return;
        }

        for (String item : field.split(",")) {
            String normalizedItem = item.trim();

            if (!normalizedItem.isEmpty()) {
                outputKey.set(category + "|" + normalizedItem);
                context.write(outputKey, ONE);
            }
        }
    }
}
