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

public class ETLMapperQuery4Step1 {

// return zero to many lines with the format [hashtag]\t[date_time],[userId]\t[tweet]\n 	
		
	public HashTagInfo[] parseJson(String line){
		JsonElement jelement = new JsonParser().parse(line);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    HashTagInfo[] result;
	    int numHashtags;

	    // get hashtags
	    try{
	    	numHashtags = jobject.getAsJsonObject("entities").getAsJsonArray("hashtags").size();
	    	if(numHashtags < 1) return null;
	    	result = new HashTagInfo[numHashtags];
	    	for (int i = 0; i < numHashtags; i++){
	    		String temp = jobject.getAsJsonObject("entities").getAsJsonArray("hashtags").get(i).getAsJsonObject().get("text").toString();
	    		temp = temp.substring(1, temp.length()-1);
	    		result[i] = new HashTagInfo();
	    		result[i].hashtags=temp;
	    	}	    	
	    }catch (Exception e){
	    	return null; // if string_id or int_id is missing, return empty string
	    }
	    
	    // get user_id
	    try{
	    	String userIdNumS = jobject.getAsJsonObject("user").get("id").toString().replaceAll("\"", "");
	    	String userIdStr = jobject.getAsJsonObject("user").get("id_str").toString().replaceAll("\"", ""); // get raw user_id
	    	if (!userIdNumS.equals(userIdStr)) {
	    		return null; // if string_id is inconsistent with int_id (malformed records), return empty string
	    	}
	    	for (int i = 0; i < numHashtags; i++){
	    		result[i].userID = Long.parseLong(userIdStr);
	    	}
	    }catch (Exception e){
	    	return null; // if string_id or int_id is missing, return empty string
	    }

	    // get date time
	    try{
			SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat fromFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
			toFormat.setTimeZone(TimeZone.getTimeZone("GMT"));	    	
	    	String timeString = jobject.get("created_at").toString().replaceAll("\"", ""); // get raw tweet_time		    		
			Date date = fromFormat.parse(timeString);			
			
			for (int i = 0; i < numHashtags; i++){
	    		result[i].dateTime = toFormat.format(date);
	    	}
	    }catch (Exception e){
	    	return null; // if tweet_time is missing or empty, return empty string 
	    }
	      
	    // get tweet
	    try{
	    	// get raw tweet_text
	    	String tweetText = jobject.get("text").toString();
            tweetText = tweetText.substring(1, tweetText.length()-1);            
            for (int i = 0; i < numHashtags; i++){
	    		result[i].tweet = tweetText;
	    	}
	    	
	    }catch (Exception e){
	    	return null; // if string_id or int_id is missing, return empty string
	    }
	    return result;
	}

	// help class to store values
	protected class HashTagInfo {
		protected String hashtags;
		protected String dateTime;
		protected long userID;
		protected String tweet;
		protected HashTagInfo(){
			hashtags = "";
			dateTime = "1970-01-01 00:00:00";
			userID = 0;
			tweet = "";
		}
		
		public String toString(){
			String result = "";
			result = hashtags + "\t" + dateTime + "," + String.valueOf(userID) + "\t" + tweet;	
			return result;
		}
	}
	
	public static void main (String[] args) {
		 
		try{
			ETLMapperQuery4Step1 pj = new ETLMapperQuery4Step1();
			BufferedReader br = 
	                      new BufferedReader(new InputStreamReader(System.in));
	        String inputLine;
	        //While we have input on stdin
			while((inputLine=br.readLine())!=null){
			    //Initialize string input
                HashTagInfo[] result = pj.parseJson(inputLine);
                if (result != null){
                    int reslen = result.length;
                    for (int i = 0; i < reslen; i++){
                    	System.out.println(result[i]);    //Output using user_id as key and the rest as value
                    }
		 	    }
			}	 
		}catch(IOException io){
			io.printStackTrace();
		}	
	}


}

