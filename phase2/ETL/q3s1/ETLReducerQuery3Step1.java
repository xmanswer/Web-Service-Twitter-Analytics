import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Comparable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.HashMap;

/* step1 reducer output will be [userid]\t[date,pos_score,tweetid,text]\t[date,neg_score,tweetid,text]*/
public class ETLReducerQuery3Step1 {
    public static void main (String args[]) {
         
        try{
            BufferedReader br = 
                          new BufferedReader(new InputStreamReader(System.in));
            //Initialize Variables
            String input;
            String lastUserID = null;
            String response = "";
            Map<ScoreAndTweetID, String> tweetMap = 
                    new TreeMap<ScoreAndTweetID, String>();
            
            //While we have input on stdin
            while((input=br.readLine())!=null){
                String[] inputSplit = input.split("\t");
                // get the current userID
                String currentUserID = inputSplit[0];
                // get comma separated text including all columns
                String responseValue = inputSplit[1]; 
                String[] responseValueSplit = responseValue.split(",");
                // get the date
                String date = responseValueSplit[0];
                // get the impact score
                String impactScore = responseValueSplit[1];
                // get the tweet ID
                String tweetID = responseValueSplit[2];
                
                ScoreAndTweetID scoreAndTweetID = 
                        new ETLReducerQuery3Step1().new ScoreAndTweetID(date, Long.parseLong(impactScore), Long.parseLong(tweetID));
            
                // if get new userID and time
                if (!currentUserID.equals(lastUserID)){                   
                    // print old results
                    
                    // sort tweets by tweetID, and print them in the correct format
                    // check if there are more than 10 tweets for a single user on one day, if so, print the 10 with the highest scores
                    Map<String, Integer> positiveDateCount = new HashMap<String, Integer>();
                    Map<String, Integer> negativeDateCount = new HashMap<String, Integer>();
                    
                    ArrayList<String> posList = new ArrayList<String>();
                    ArrayList<String> negList = new ArrayList<String>();
                    
                    Set<Entry<ScoreAndTweetID, String>> idSet = tweetMap.entrySet();
                    Iterator<Entry<ScoreAndTweetID, String>> iterator = idSet.iterator();
                    while(iterator.hasNext()){
                        Map.Entry<ScoreAndTweetID, String> me = (Map.Entry<ScoreAndTweetID, String>)iterator.next();
                        ScoreAndTweetID currScoreAndTweetID = (ScoreAndTweetID)me.getKey();
                        if (currScoreAndTweetID.score > 0) { // for positive score                            
                            if (positiveDateCount.containsKey(currScoreAndTweetID.date)) { // repeated date
                                positiveDateCount.put(currScoreAndTweetID.date, new Integer(positiveDateCount.get(currScoreAndTweetID.date).intValue() + 1));
                                if (positiveDateCount.get(currScoreAndTweetID.date).intValue() <= 10){  // if more than 10 tweets are there for one day, just print the first 10
                                	posList.add(me.getValue() + "\"15619sucksnewline\"");
                                }
                            } 
                            else { // new date
                                positiveDateCount.put(currScoreAndTweetID.date, new Integer(1)); 
                                posList.add(me.getValue() + "\"15619sucksnewline\"");
                            }
                        }
                        else {  // for negative score
                            if (negativeDateCount.containsKey(currScoreAndTweetID.date)) { // repeated date
                                negativeDateCount.put(currScoreAndTweetID.date, new Integer(negativeDateCount.get(currScoreAndTweetID.date).intValue() + 1));
                                if (negativeDateCount.get(currScoreAndTweetID.date).intValue() <= 10){  // if more than 10 tweets are there for one day, just print the first 10
                                    negList.add(me.getValue() + "\"15619sucksnewline\"");
                                }
                            } 
                            else { // new date
                                negativeDateCount.put(currScoreAndTweetID.date, new Integer(1)); 
                                negList.add(me.getValue() + "\"15619sucksnewline\"");
                            }
                        }
                    }
                    if (lastUserID!=null) {
                    	StringBuilder sbpos = new StringBuilder();
                    	StringBuilder sbneg = new StringBuilder();
                    	for(String str : posList) {
                    		sbpos.append(str);
                    	}
                    	for(String str : negList) {
                    		sbneg.append(str);
                    	}
                        System.out.println(lastUserID + "\t" + sbpos.toString() + "\t" + sbneg.toString());
                    }
                    // create new empty map and new empty response
                    tweetMap = new TreeMap<ScoreAndTweetID, String>();
                }
                
                tweetMap.put(scoreAndTweetID, responseValue); // can handle duplicates
                lastUserID = currentUserID;
            }
            
            // print last line
            
            Map<String, Integer> positiveDateCount = new HashMap<String, Integer>();
            Map<String, Integer> negativeDateCount = new HashMap<String, Integer>();
            
            ArrayList<String> posList = new ArrayList<String>();
            ArrayList<String> negList = new ArrayList<String>();
            
            Set<Entry<ScoreAndTweetID, String>> idSet = tweetMap.entrySet();
            Iterator<Entry<ScoreAndTweetID, String>> iterator = idSet.iterator();

            while(iterator.hasNext()){
                Map.Entry<ScoreAndTweetID, String> me = (Map.Entry<ScoreAndTweetID, String>)iterator.next();
                ScoreAndTweetID currScoreAndTweetID = (ScoreAndTweetID)me.getKey();
                if (currScoreAndTweetID.score > 0) { // for positive score
                    if (positiveDateCount.containsKey(currScoreAndTweetID.date)) { // repeated date
                        positiveDateCount.put(currScoreAndTweetID.date, new Integer(positiveDateCount.get(currScoreAndTweetID.date).intValue() + 1));
                        if (positiveDateCount.get(currScoreAndTweetID.date).intValue() <= 10){  // if more than 10 tweets are there for one day, just print the first 10
                        	posList.add(me.getValue() + "\"15619sucksnewline\"");
                        }
                    } 
                    else { // new date
                        positiveDateCount.put(currScoreAndTweetID.date, new Integer(1)); 
                        posList.add(me.getValue() + "\"15619sucksnewline\"");
                    }
                }
                else {  // for negative score
                    if (negativeDateCount.containsKey(currScoreAndTweetID.date)) { // repeated date
                        negativeDateCount.put(currScoreAndTweetID.date, new Integer(negativeDateCount.get(currScoreAndTweetID.date).intValue() + 1));
                        if (negativeDateCount.get(currScoreAndTweetID.date).intValue() <= 10){  // if more than 10 tweets are there for one day, just print the first 10
                        	negList.add(me.getValue() + "\"15619sucksnewline\"");
                        }
                    } 
                    else { // new date
                        negativeDateCount.put(currScoreAndTweetID.date, new Integer(1)); 
                        negList.add(me.getValue() + "\"15619sucksnewline\"");                       
                    }
                }
            }
            
            StringBuilder sbpos = new StringBuilder();
        	StringBuilder sbneg = new StringBuilder();
        	for(String str : posList) {
        		sbpos.append(str);
        	}
        	for(String str : negList) {
        		sbneg.append(str);
        	}
            System.out.println(lastUserID + "\t" + sbpos.toString() + "\t" + sbneg.toString());

        }catch(Exception e){
            e.printStackTrace();
        }   
    }
    
    /* helper class for score and tweetID */
    protected class ScoreAndTweetID implements Comparable<ScoreAndTweetID> {
        protected String date;
        protected long score;
        protected long tweetID;
        
        protected ScoreAndTweetID(String date, long score, long tweetID) {
            this.date = date;
            this.score = score;
            this.tweetID = tweetID;
        }

        @Override
        public int compareTo(ScoreAndTweetID second) {
            if (this.score > second.score && this.score > 0 && second.score > 0) { //des order of score
                return -1;
            } else if (this.score < second.score && this.score > 0 && second.score > 0) {
                return 1;
            } else if (this.score > second.score && this.score < 0 && second.score < 0){
                return 1;
            } else if (this.score < second.score && this.score < 0 && second.score < 0){
                return -1;
            } else if (this.score < 0 && second.score > 0){
                return 1;
            } else if (this.score > 0 && second.score < 0){ 
                return -1;
            } else { //if tie
                if(this.tweetID > second.tweetID) { //asc order of tweetID
                    return 1;
                } else if (this.tweetID < second.tweetID) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }

    }
    

}
