import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Comparable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import java.util.Iterator;
import java.util.HashMap;

/* step2 reducer output format will be [userid]\t[date,score,tweetid,text]\t[hashkey] */
public class ETLReducerQuery3Step2 {
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
