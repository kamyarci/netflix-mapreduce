
package br.com.kamyla.netflix;

import br.com.kamyla.netflix.analysis.AmericanDramaDescriptionMapper;
import br.com.kamyla.netflix.reducer.WordCountReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class NetflixAmericanDramaDescription {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println(
                    "Uso: NetflixAmericanDramaDescription <entrada> <saida>"
            );
            System.exit(1);
        }

        Job job = Job.getInstance(
                new Configuration(),
                "Netflix - Palavras dos Dramas Americanos"
        );

        job.setJarByClass(NetflixAmericanDramaDescription.class);

        job.setMapperClass(AmericanDramaDescriptionMapper.class);
        job.setCombinerClass(WordCountReducer.class);
        job.setReducerClass(WordCountReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
