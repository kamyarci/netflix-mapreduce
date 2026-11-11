
package br.com.kamyla.netflix.analysis;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.Set;

public class CountryActorMapper
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

            String countriesField = record.get(5).trim();
            String castField = record.get(4).trim();

            if (countriesField.isEmpty() || castField.isEmpty()) {
                context.getCounter(
                        "NETFLIX", "MISSING_COUNTRY_OR_CAST"
                ).increment(1);
                return;
            }

            Set<String> countries = splitValues(countriesField);
            Set<String> actors = splitValues(castField);

            for (String country : countries) {
                for (String actor : actors) {
                    outputKey.set(country + "|" + actor);
                    context.write(outputKey, ONE);
                }
            }

        } catch (Exception e) {
            context.getCounter(
                    "NETFLIX", "INVALID_CSV_RECORDS"
            ).increment(1);
        }
    }

    private Set<String> splitValues(String field) {
        Set<String> values = new LinkedHashSet<>();

        for (String item : field.split(",")) {
            String normalized = item.trim();

            if (!normalized.isEmpty()) {
                values.add(normalized);
            }
        }

        return values;
    }
}
