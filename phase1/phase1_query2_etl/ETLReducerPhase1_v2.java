import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Iterator;


public class ETLReducerPhase1_v2 {
	public static void main (String args[]) {
		 
		try{
			BufferedReader br = 
	                      new BufferedReader(new InputStreamReader(System.in));
	        //Initialize Variables
	        String input;
	        String lastUserIDAndTime = null;
	        String response = "";
	        Map<String, String> tweetMap = new TreeMap<String, String>();
	        //While we have input on stdin
			while((input=br.readLine())!=null){
				String[] inputSplit = input.split("\t");
                // get the current userID and time
				String currentUserIDAndTime = inputSplit[0];
                // get the tweet ID, sort the tweets by tweet ID
				String[] currentResponseSplit = inputSplit[1].split(":");
				String tweetID = currentResponseSplit[0];
				//int tweetLength = currentResponseSplit[0].length();  /////////////
				//String inserted = inputSplit[1].substring(tweetLength, inputSplit[1].length());   //////////
                String inserted = inputSplit[1];
                
                // if get new userID and time
                if (!currentUserIDAndTime.equals(lastUserIDAndTime)){
                    // print old results
                    
                    // sort tweets by tweetID, and print them in the correct format
                    Set idSet = tweetMap.entrySet();
                    Iterator iterator = idSet.iterator();
                    while(iterator.hasNext()){
                        Map.Entry me = (Map.Entry)iterator.next();
                        response = response + me.getValue() + "\"15619sucksnewline\"";
                    }
                    if (lastUserIDAndTime!=null) {
                        System.out.println(lastUserIDAndTime + "\t" + response);
                    }
                    // create new empty map and new empty response
                    tweetMap = new TreeMap<String, String>();
                    response = "";
                }
                
				tweetMap.put(tweetID, inserted); // can handle duplicates
                lastUserIDAndTime = currentUserIDAndTime;
	        }
            
            // print last line
            Set idSet = tweetMap.entrySet();
            Iterator iterator = idSet.iterator();
            while(iterator.hasNext()){
                Map.Entry me = (Map.Entry)iterator.next();
                response = response + me.getValue() + "\"15619sucksnewline\"";
            }
            System.out.println(lastUserIDAndTime + "\t" + response);

		}catch(Exception e){
			e.printStackTrace();
		}	
	  }
}
