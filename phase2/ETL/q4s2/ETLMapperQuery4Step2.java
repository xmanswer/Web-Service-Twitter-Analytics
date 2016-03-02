import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/* input form is [hashtag]\t[a list of date,count,list_of_user_id,source_tweet]\n
 * output form is [hashkey 1]\t[hashtag]\t[the list]\n                           */

public class ETLMapperQuery4Step2 {
	public static void main (String[] args) {
		try{
			BufferedReader br = 
	                      new BufferedReader(new InputStreamReader(System.in));
	        String inputLine;
	        //While we have input on stdin
			while((inputLine=br.readLine())!=null){
			    //Initialize string input
				String[] inputSplit = inputLine.split("\t");
				String hashtag = inputSplit[0];
				String response = inputSplit[1];
				int newKeyPos = hashtag.hashCode() % 8 + 1; //key in range of 1 to 8
                String resultS = Integer.toString(newKeyPos) + '\t' + hashtag + '\t' + response;
                if (resultS != null){
                    System.out.println(resultS);    
		 	    }
                
			}	 
		}catch(IOException io){
			io.printStackTrace();
		}	
	}
}
