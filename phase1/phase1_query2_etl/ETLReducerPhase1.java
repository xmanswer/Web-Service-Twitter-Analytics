import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ETLReducerPhase1 {
	public static void main (String args[]) {
		 
		try{
			BufferedReader br = 
	                      new BufferedReader(new InputStreamReader(System.in));
	        //Initialize Variables
	        String input;
	        //While we have input on stdin
			while((input=br.readLine())!=null){	                
	            //We have sorted input, so check if we
				System.out.println(input);
	        }	          
		}catch(Exception e){
			e.printStackTrace();
		}	
	  }
}
