
package br.com.kamyla.netflix;

import br.com.kamyla.netflix.analysis.CountryActorMapper;
import br.com.kamyla.netflix.reducer.WordCountReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class NetflixCountryActor {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println(
                    "Uso: NetflixCountryActor <entrada> <saida>"
            );
            System.exit(1);
        }

        Configuration configuration = new Configuration();

        Job job = Job.getInstance(
                configuration,
                "Netflix - Atores por Pais"
        );

        job.setJarByClass(NetflixCountryActor.class);

        job.setMapperClass(CountryActorMapper.class);
        job.setCombinerClass(WordCountReducer.class);
        job.setReducerClass(WordCountReducer.class);

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

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
