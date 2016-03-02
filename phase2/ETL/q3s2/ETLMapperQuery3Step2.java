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
import java.util.TimeZone;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/* input form is [userid]\t[date,pos_score,tweetid,text]\t[date,neg_score,tweetid,text]
 * output form is [hashkey 1]\t[userid]\t[date,pos_score,tweetid,text]\n
 * 				  [hashkey 2]\t[userid]\t[date,neg_score,tweetid,text]\n               */

public class ETLMapperQuery3Step2 {
	
	public static void main (String[] args) {
		try{
			BufferedReader br = 
	                      new BufferedReader(new InputStreamReader(System.in));
	        String inputLine;
	        //While we have input on stdin
			while((inputLine=br.readLine())!=null){
			    //Initialize string input
				String[] inputSplit = inputLine.split("\t");
				//System.out.println(inputSplit.length);
				if (inputSplit.length == 2){ // only positive tweets						
					String userid = inputSplit[0];
					String posResponse = inputSplit[1];
					int newKeyPos = userid.hashCode() % 8 + 1; //pos key in range of 1 to 8
	                String resultPos = Integer.toString(newKeyPos) + '\t' + userid + '\t' + posResponse;
                    System.out.println(resultPos);    //Output using user_id as key and the rest as value
				} 
				else if (inputSplit[1].equals("")){  // only negative tweets
					String userid = inputSplit[0];
					String negResponse = inputSplit[2];
					int newKeyPos = userid.hashCode() % 8 + 1; //pos key in range of 1 to 8
					int newKeyNeg = newKeyPos + 8; //neg key in range of 9 to 16
                	String resultNeg = Integer.toString(newKeyNeg) + '\t' + userid + '\t' + negResponse;
                	if (resultNeg != null){
                		System.out.println(resultNeg);    //Output using user_id as key and the rest as value
		 	    	}
				}	
				else{
					String userid = inputSplit[0];
					String posResponse = inputSplit[1];
					String negResponse = inputSplit[2];
					int newKeyPos = userid.hashCode() % 8 + 1; //pos key in range of 1 to 8
					int newKeyNeg = newKeyPos + 8; //neg key in range of 9 to 16
	                String resultPos = Integer.toString(newKeyPos) + '\t' + userid + '\t' + posResponse;
	                String resultNeg = Integer.toString(newKeyNeg) + '\t' + userid + '\t' + negResponse;
	                if (resultPos != null){
	                    System.out.println(resultPos);    //Output using user_id as key and the rest as value
			 	    }
	                if (resultNeg != null){
	                	System.out.println(resultNeg);    //Output using user_id as key and the rest as value
			 	    }
				}
			}	 
		}catch(IOException io){
			io.printStackTrace();
		}	
	}
}
