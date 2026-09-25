package br.com.kamyla.netflix.preprocessing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvNormalizer {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println(
                    "Uso: CsvNormalizer <arquivo-entrada> <arquivo-saida>"
            );
            System.exit(1);
        }

        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);

        try (
                BufferedReader reader =
                        Files.newBufferedReader(input, StandardCharsets.UTF_8);

                CSVParser parser =
                        CSVFormat.DEFAULT.parse(reader);

                BufferedWriter writer =
                        Files.newBufferedWriter(output, StandardCharsets.UTF_8);

                CSVPrinter printer =
                        new CSVPrinter(writer, CSVFormat.DEFAULT)
        ) {

            long records = 0;

            for (CSVRecord record : parser) {

                String[] fields = new String[record.size()];

                for (int i = 0; i < record.size(); i++) {
                    fields[i] = record.get(i)
                            .replace("\r", " ")
                            .replace("\n", " ");
                }

                printer.printRecord((Object[]) fields);
                records++;
            }

            System.out.println(
                    "Registros gravados: " + records
            );
        }
    }
}
