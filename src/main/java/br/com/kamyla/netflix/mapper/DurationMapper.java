
package br.com.kamyla.netflix.mapper;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final Pattern DURATION_PATTERN =
            Pattern.compile("^(\\d+)\\s+(min|Seasons?)$");

    private final Text outputKey = new Text();
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
                context.getCounter(
                        "NETFLIX", "INVALID_CSV_RECORDS"
                ).increment(1);
                return;
            }

            String type = record.get(1).trim();
            String duration = record.get(9).trim();

            Matcher matcher = DURATION_PATTERN.matcher(duration);

            if (!matcher.matches()) {
                context.getCounter(
                        "NETFLIX", "SEM_DURACAO"
                ).increment(1);
                return;
            }

            int number = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            if (number <= 0) {
                context.getCounter(
                        "NETFLIX", "INVALID_DURATION"
                ).increment(1);
                return;
            }

            if (type.equals("Movie") && unit.equals("min")) {
                outputKey.set("FILME");
            } else if (type.equals("TV Show")
                    && (unit.equals("Season") || unit.equals("Seasons"))) {
                outputKey.set("SERIE");
            } else {
                context.getCounter(
                        "NETFLIX", "INVALID_DURATION_TYPE"
                ).increment(1);
                return;
            }

            outputValue.set(number);
            context.write(outputKey, outputValue);

        } catch (Exception e) {
            context.getCounter(
                    "NETFLIX", "INVALID_CSV_RECORDS"
            ).increment(1);
        }
    }
}
