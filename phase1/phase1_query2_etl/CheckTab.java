import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CheckTab {
	

    public CheckTab(){
    }
			
	public void parseJson(String line){
		JsonElement jelement = new JsonParser().parse(line);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    String result = "";
	    int sentimentScore = 0;

	    // handle tweet content
	    try{
	    	// get raw tweet_text
	    	String tweetText = jobject.get("text").toString();
            tweetText = tweetText.substring(1, tweetText.length()-1);
            
            char character = '\n';
            for (char c : tweetText.toCharArray()){
            	if (c == character)
            		System.out.println("It has RETURN!!!!!");
            }
            
	    }catch (Exception e){

	    }
	    		
	}		
		
				
	public static void main (String[] args) {
		 
		try{
            CheckTab pj = new CheckTab();
			BufferedReader br = 
	                      new BufferedReader(new InputStreamReader(System.in));
	        String inputLine;
			while((inputLine=br.readLine())!=null){
	        	    pj.parseJson(inputLine);
			}	 
		}catch(IOException io){
			io.printStackTrace();
		}	
	}

	
}
