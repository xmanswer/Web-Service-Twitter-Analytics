import java.io.IOException;
import java.util.StringTokenizer;
import java.lang.*;
import java.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class q5emr {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, Text>{

    private Text map_k = new Text();
    private Text map_v = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      String line = value.toString();
      if (line.length() == 0)
        return;

      String user_id = null;
      String tweet_id = null;

	try {
	    JsonElement jelement = new JsonParser().parse(line);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    
	    // handle user_id
	    try{
	    	String userIdNumS = jobject.getAsJsonObject("user").get("id").toString().replaceAll("\"", "");
	    	//long userIdNum = Long.parseLong(userIdNumS);
	    	String userIdStr = jobject.getAsJsonObject("user").get("id_str").toString().replaceAll("\"", ""); // get raw user_id
	    	if (!userIdNumS.equals(userIdStr)) {
	    		return; // if string_id is inconsistent with int_id (malformed records), return empty string
	    	}
	    	user_id = userIdStr;
	    }catch (Exception e){
	    	return; // if string_id or int_id is missing, return empty string
	    }


	    // handle tweet_id
	    try{
		    String tweetIdNumS = jobject.get("id").toString().replaceAll("\"", ""); 
		    //long tweetIdNum = Long.parseLong(tweetIdNumS);  
		    String tweetIdStr = jobject.get("id_str").toString().replaceAll("\"", ""); // raw tweet_id
		    if (!tweetIdNumS.equals(tweetIdStr)){  // if the numerical tweet_id does not equal string tweet_id, return empty string
		    	return;
		    }
		    tweet_id = tweetIdStr;
	    }catch (Exception e){
	    	return; //if tweet_id is missing, return empty string
	    }
	} catch (Exception e) {
		return;
	}

      if (user_id == null || tweet_id == null)
	return;

        map_k.set(user_id);
        map_v.set(tweet_id);
        context.write(map_k, map_v);
    }
  }

  public static class IntSumReducer
       extends Reducer<Text,Text,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
      Map<String, String> ht = new HashMap<String, String>();
      // count each occurance
      for (Text val : values) {
        String tmp = val.toString();
        if (ht.containsKey(tmp) == false) 
          ht.put(tmp, "");
      }

      result.set(ht.size());
      // output the key and value
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "q5emr");
    
    job.setJarByClass(q5emr.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setReducerClass(IntSumReducer.class);
    
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}

