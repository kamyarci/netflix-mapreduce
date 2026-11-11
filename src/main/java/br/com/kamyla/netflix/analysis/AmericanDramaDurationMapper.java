
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmericanDramaDurationMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final Pattern DURATION =
            Pattern.compile("^(\\d+)\\s+min$");

    private static final Text DRAMA = new Text("DRAMA_AMERICANO");

    private final IntWritable outputValue = new IntWritable();

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

            String type = record.get(1).trim();

            boolean isAmerican = Arrays.stream(record.get(5).split(","))
                    .map(String::trim)
                    .anyMatch("United States"::equals);

            boolean isDrama = Arrays.stream(record.get(10).split(","))
                    .map(String::trim)
                    .anyMatch("Dramas"::equals);

            if (!type.equals("Movie") || !isAmerican || !isDrama) {
                return;
            }

            Matcher matcher = DURATION.matcher(record.get(9).trim());

            if (!matcher.matches()) {
                context.getCounter(
                        "NETFLIX", "DRAMA_AMERICANO_SEM_DURACAO"
                ).increment(1);
                return;
            }

            int minutes = Integer.parseInt(matcher.group(1));

            if (minutes <= 0) {
                context.getCounter(
                        "NETFLIX", "DRAMA_AMERICANO_DURACAO_INVALIDA"
                ).increment(1);
                return;
            }

            outputValue.set(minutes);
            context.write(DRAMA, outputValue);

        } catch (Exception e) {
            context.getCounter("NETFLIX", "INVALID_CSV_RECORDS")
                    .increment(1);
        }
    }
}
