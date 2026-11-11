
package br.com.kamyla.netflix;

import br.com.kamyla.netflix.analysis.AmericanDramaDurationMapper;
import br.com.kamyla.netflix.reducer.DurationReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class NetflixAmericanDramaDuration {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println(
                    "Uso: NetflixAmericanDramaDuration <entrada> <saida>"
            );
            System.exit(1);
        }

        Job job = Job.getInstance(
                new Configuration(),
                "Netflix - Duracao dos Dramas Americanos"
        );

        job.setJarByClass(NetflixAmericanDramaDuration.class);

        job.setMapperClass(AmericanDramaDurationMapper.class);
        job.setReducerClass(DurationReducer.class);

        job.setNumReduceTasks(1);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
