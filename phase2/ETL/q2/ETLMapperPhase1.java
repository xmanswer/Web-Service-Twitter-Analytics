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

public class ETLMapperPhase1 {
	
	private SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
	private SimpleDateFormat fromFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
	private Date dateThre; 
	private Map<String, Integer> sentimentDict;
	private Set<String> censorWords;
	private BufferedReader br;
	
	public ETLMapperPhase1(String sentimentFile, String censorFile, String dateStr){
        toFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateThre = initializeDateThre(dateStr);
		sentimentDict = initializeSentiment(sentimentFile);
		censorWords = initializeCensor(censorFile);	
	}
		
	private Date initializeDateThre(String dateThre){
		try{
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = inputFormat.parse(dateThre);
			return date;
		} catch(Exception e){
            System.out.println("Cannot parse the earlist date!");
			return null;
		}				
	}
		
	private Map<String, Integer> initializeSentiment(String sFile){
		Map<String, Integer> sDict = new HashMap<String, Integer>();
		try{
			br = new BufferedReader(new FileReader(sFile));
			String strLine = "";
			while ((strLine = br.readLine()) != null) {
				String[] s= strLine.split("\t");
				sDict.put(s[0], Integer.valueOf(s[1]));
			}
			return sDict;
		} catch(Exception e){
			System.out.println("Cannot create the sentiment dictionary!");
			return null;
		}
	}
		
	private Set<String> initializeCensor(String cFile){
		Set<String> cDict = new HashSet<String>();
		try{
			br = new BufferedReader(new FileReader(cFile));
			String strLine = "";
			while ((strLine = br.readLine()) != null) {
				char[] strChars= strLine.toCharArray();
				for (int i = 0; i < strChars.length; i++){
					if(Character.isLetter(strChars[i])){
						strChars[i] = (char) ((strChars[i] + 13 - (int)'a') % 26 + (int)'a');
					}
				}
				String strLine1 = new String(strChars);	
				cDict.add(strLine1);
			}
			return cDict;
		} catch(Exception e){
			System.out.println("Cannot create the censor dictionary!");
			return null;
		}
	}
		
	public String parseJson(String line){
		JsonElement jelement = new JsonParser().parse(line);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    String result = "";
	    int sentimentScore = 0;

	    // handle user_id
	    try{
	    	String userIdNumS = jobject.getAsJsonObject("user").get("id").toString().replaceAll("\"", "");
	    	//long userIdNum = Long.parseLong(userIdNumS);
	    	String userIdStr = jobject.getAsJsonObject("user").get("id_str").toString().replaceAll("\"", ""); // get raw user_id
	    	if (!userIdNumS.equals(userIdStr)) {
	    		return null; // if string_id is inconsistent with int_id (malformed records), return empty string
	    	}
	    	result = result + userIdStr + "+";
	    }catch (Exception e){
	    	return null; // if string_id or int_id is missing, return empty string
	    }
	    	    
	    // handle tweet_time
	    try{
		    String timeString = jobject.get("created_at").toString().replaceAll("\"", ""); // get raw tweet_time		    		
			Date date = fromFormat.parse(timeString);
			if (date.compareTo(dateThre) < 0) {
				return null; //if Tweet is prior to 2014-04-20, return empty string
			}
			timeString = toFormat.format(date);
		    result = result + timeString + "\t";
	    }catch (Exception e){
	    	return null; // if tweet_time is missing or empty, return empty string 
	    }
	    
	    //UserId_timeStamp is the mapreduce key
	    
	    // handle tweet_id
	    try{
		    String tweetIdNumS = jobject.get("id").toString().replaceAll("\"", ""); 
		    //long tweetIdNum = Long.parseLong(tweetIdNumS);  
		    String tweetIdStr = jobject.get("id_str").toString().replaceAll("\"", ""); // raw tweet_id
		    if (!tweetIdNumS.equals(tweetIdStr)){  // if the numerical tweet_id does not equal string tweet_id, return empty string
		    	return null;
		    }
		    result = result + tweetIdStr + ":";
	    }catch (Exception e){
	    	return null; //if tweet_id is missing, return empty string
	    }

	    // handle tweet content
	    try{
	    	// get raw tweet_text
	    	String tweetText = jobject.get("text").toString();
            tweetText = tweetText.substring(1, tweetText.length()-1);
            
                
	    	StringBuilder newText = new StringBuilder(tweetText);
	    	// split into "words" whenever non-alphanumeric characters occur
	    	String[] tweetWords = tweetText.replaceAll("\\\\n", "#").split("[^a-zA-Z0-9']");
	    	//String[] tweetWords = tweetText.split("[^a-zA-Z0-9']");
	    	int startingIndex = 0;
	    	int endingIndex = 0;
	    	int cumuStart = 0;

	    	for (int i = 0; i < tweetWords.length; i++){
	    		String s = tweetWords[i];

	    		if (s.length() == 0) {
	    			continue;
	    		}
	    		
		    	while ( s.charAt(0) != tweetText.charAt(cumuStart)) {
	    			cumuStart ++;
	    		}
	    		startingIndex = cumuStart;
	    		endingIndex = startingIndex + s.length() - 1;
	    		cumuStart = endingIndex + 1;
	    		
	    		if (s.length() > 0) { // if the current string is empty, leave as it is
	    			s = s.toLowerCase(); // otherwise convert the current "word" to lower case
	    			// get sentiment score
	    			if (sentimentDict.containsKey(s)) {
	    				sentimentScore += sentimentDict.get(s);  
	    			}
	    			// replace the censored words
	    			if (censorWords.contains(s)) {	    				
	    				for (int j = (startingIndex + 1); j < endingIndex; j++){
	    					newText.setCharAt(j,'*');
	    				}
	    			}
	    		}	
	    	}
	    	// combine the censored strings back to the text
	    	tweetText = newText.toString();
	    	result = result + Integer.toString(sentimentScore) + ":" + tweetText;
	    }catch (Exception e){
	    	return null; 
	    }
	    		
		return result;
	}		
		
				
	public static void main (String[] args) {
		 
		try{
            String sFile = args[0];
			String cFile = args[1];
			String earlistDate = "2014-04-20 00:00:00";
			ETLMapperPhase1 pj = new ETLMapperPhase1(sFile, cFile, earlistDate);
			BufferedReader br = 
	                      new BufferedReader(new InputStreamReader(System.in));
	        String inputLine;
	        //While we have input on stdin
			while((inputLine=br.readLine())!=null){
			    //Initialize string input
                String result = pj.parseJson(inputLine);
                if (result != null){
                    System.out.println(result);    //Output using user_id as key and the rest as value
	        
		 	    }
			}	 
		}catch(IOException io){
			io.printStackTrace();
		}	
	}

	
}
