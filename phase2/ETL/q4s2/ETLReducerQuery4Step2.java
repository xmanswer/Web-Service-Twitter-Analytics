import java.io.BufferedReader;
import java.io.InputStreamReader;

/* step2 reducer output format will be [hashtag]\t[response]\t[hashkey] */
public class ETLReducerQuery4Step2 {
    public static void main (String args[]) {
        
        try{
            BufferedReader br = 
                          new BufferedReader(new InputStreamReader(System.in));
            String input;
            //While we have input on stdin
            while((input=br.readLine())!=null){ //put the hash index at the end
                String[] inputSplit = input.split("\t");
                System.out.println(inputSplit[1] + "\t" + inputSplit[2] + "\t" + inputSplit[0]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }   
    }

}
