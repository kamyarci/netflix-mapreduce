
package br.com.kamyla.netflix.analysis;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CountryGenreDirectorMapper
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

            Set<String> countries = splitValues(record.get(5));
            Set<String> genres = splitValues(record.get(10));
            Set<String> directors = splitValues(record.get(3));

            if (countries.isEmpty()
                    || genres.isEmpty()
                    || directors.isEmpty()) {

                context.getCounter(
                        "NETFLIX", "MISSING_DIRECTOR_CROSS_FIELDS"
                ).increment(1);
                return;
            }

            for (String country : countries) {
                for (String genre : genres) {
                    for (String director : directors) {
                        outputKey.set(
                                country + "|" + genre + "|" + director
                        );
                        context.write(outputKey, ONE);
                    }
                }
            }

        } catch (Exception e) {
            context.getCounter(
                    "NETFLIX", "INVALID_CSV_RECORDS"
            ).increment(1);
        }
    }

    private Set<String> splitValues(String field) {
        return Arrays.stream(field.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
