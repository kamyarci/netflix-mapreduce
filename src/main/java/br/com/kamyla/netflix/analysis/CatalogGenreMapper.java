
package br.com.kamyla.netflix.analysis;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.io.StringReader;

public class CatalogGenreMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final IntWritable ONE = new IntWritable(1);
    private final Text genreKey = new Text();

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

            String genres = record.get(10).trim();

            if (genres.isEmpty()) {
                context.getCounter(
                        "NETFLIX", "MISSING_GENRES"
                ).increment(1);
                return;
            }

            for (String genre : genres.split(",")) {
                String normalizedGenre = genre.trim();

                if (!normalizedGenre.isEmpty()) {
                    genreKey.set(normalizedGenre);
                    context.write(genreKey, ONE);
                }
            }

        } catch (Exception e) {
            context.getCounter(
                    "NETFLIX", "INVALID_CSV_RECORDS"
            ).increment(1);
        }
    }
}
