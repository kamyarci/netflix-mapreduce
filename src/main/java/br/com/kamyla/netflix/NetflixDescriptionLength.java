package br.com.kamyla.netflix;

import br.com.kamyla.netflix.mapper.DescriptionLengthMapper;
import br.com.kamyla.netflix.reducer.DescriptionLengthReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class NetflixDescriptionLength {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println(
                    "Uso: NetflixDescriptionLength <input> <output>"
            );
            System.exit(1);
        }

        Configuration configuration = new Configuration();

        Job job = Job.getInstance(
                configuration,
                "Netflix - Tamanho das Descricoes"
        );

        job.setJarByClass(NetflixDescriptionLength.class);

        job.setMapperClass(DescriptionLengthMapper.class);
        job.setReducerClass(DescriptionLengthReducer.class);

        job.setNumReduceTasks(1);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(
                job,
                new Path(args[0])
        );

        FileOutputFormat.setOutputPath(
                job,
                new Path(args[1])
        );

        System.exit(
                job.waitForCompletion(true) ? 0 : 1
        );
    }
}
