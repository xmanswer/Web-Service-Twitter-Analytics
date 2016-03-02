import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Arrays;


public class ETLReducerQuery4Step1 {

	public static void main(String args[]) {
		try {
			// the output format is [hashtag]\t[response_string_sort_by_count]\n
			SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String lastHashtag = null;
			String input;
			String output;
			Map<Date, LinePerDate> mapPerHashtag = new HashMap<Date, LinePerDate>();
			
			// While we have input on stdin
			while ((input = br.readLine()) != null) {
				// get the tweet ID
				String[] inputSplit = input.split("\t");
				String currHashtag = inputSplit[0];
				
				String[] currentResponseSplit = inputSplit[1].split(",");
				String datetime = currentResponseSplit[0];
				Date currDate = toFormat.parse(datetime);
				Date currDateTime = fromFormat.parse(datetime);
				long userID = Long.parseLong(currentResponseSplit[1]);
				String currTweet = inputSplit[2];
				if (!currHashtag.equals(lastHashtag)){    // if get new hashtag
					// print old results
					// get a set of values, print them with a certain format
					output = lastHashtag + "\t";
					//LinePerDate[] sortedHash = (LinePerDate[])mapPerHashtag.values().toArray();
					Object[] sortedHash = mapPerHashtag.values().toArray();
					Arrays.sort(sortedHash);
					int len= sortedHash.length;
					//Set idSet = mapPerHashtag.entrySet();
					//Iterator iterator = idSet.iterator();
					//while(iterator.hasNext()){
					for (int i = 0; i < len; i++) {
						//Map.Entry me = (Map.Entry)iterator.next();
						output = output + sortedHash[i] + "\"15619sucksnewline\"";
						//output = output + me.getValue() + "\"15619sucksnewline\"";
					}
					if (lastHashtag != null){
						System.out.println(output);
					}
					// insert into new hashmap
					mapPerHashtag = new HashMap<Date, LinePerDate>();
					LinePerDate linePerDate = new ETLReducerQuery4Step1().new LinePerDate(currDateTime, userID, currTweet);
					mapPerHashtag.put(currDate, linePerDate);
				}	
				else{  // if existing hashtag
					if(mapPerHashtag.containsKey(currDate)){ // if date in mapPerHashtag
						LinePerDate currToken = (LinePerDate) mapPerHashtag.get(currDate);
						currToken.count += 1;
						currToken.userIDs.add(userID);
						currToken.setEarlistDate(currDateTime, currTweet);
					}
					else{ // if new date, create new linePerDate
						LinePerDate linePerDate = new ETLReducerQuery4Step1().new LinePerDate(currDateTime, userID, currTweet);
						mapPerHashtag.put(currDate, linePerDate);
					}
				}
				lastHashtag = currHashtag;	 
				
			}
			
			// print last hashtag
			output = lastHashtag + "\t";
			
			//LinePerDate[] sortedHash = (LinePerDate[])mapPerHashtag.values().toArray();
			Object[] sortedHash = mapPerHashtag.values().toArray();
			Arrays.sort(sortedHash);
			int len= sortedHash.length;
			//Set idSet = mapPerHashtag.entrySet();
			//Iterator iterator = idSet.iterator();
			//while(iterator.hasNext()){
			for (int i = 0; i < len; i++) {
				//Map.Entry me = (Map.Entry)iterator.next();
				output = output + sortedHash[i] + "\"15619sucksnewline\"";
				//output = output + me.getValue() + "\"15619sucksnewline\"";
			}

			if (lastHashtag != null){
				System.out.println(output);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* help class for score and tweetID */
	protected class LinePerDate implements Comparable<LinePerDate> {
		protected Date dateTime;
		protected int count;
		protected Set<Long> userIDs = new TreeSet<Long>();
		protected Date earlistDate;
		protected String earlistTweet;

		protected LinePerDate(Date currDateTime, long userID, String currTweet) {
			this.dateTime = currDateTime;
			this.count = 1;
			this.earlistDate = currDateTime;
			this.earlistTweet = currTweet;
			this.userIDs.add(new Long(userID));
		}

		@Override
		public int compareTo(LinePerDate second) {
			if (this.count > second.count) { // des order of count
				return -1;
			} else if (this.count < second.count) {
				return 1;
			} else { // if tie
				if (this.dateTime.before(second.dateTime)) { // asc order of date
					return -1;
				} else if (this.dateTime.after(second.dateTime)) {
					return 1;
				} else {
					return 0;
				}
			}
		}

		public void setEarlistDate(Date currDate, String currTweet){
			if (earlistDate.after(currDate)){
				earlistDate = currDate;
				earlistTweet = currTweet;
			}
			else if (earlistDate.equals(currDate)){
				if (earlistTweet.compareTo(currTweet)>0){
					earlistTweet = currTweet; // solve a tie by using the string by alphabetical order
				}
			}
		}
		
		public String toString() {
			SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
			String printedDate = toFormat.format(this.dateTime);
			String result = null;
			String listOfUser = "";
			Iterator<Long> iterator = userIDs.iterator();
			while (iterator.hasNext()) {
				listOfUser = listOfUser + iterator.next() + ","; // comma separated user ids
			}
			listOfUser = listOfUser.substring(0, listOfUser.length() - 1);
			result = printedDate + ":" + Integer.toString(count) + ":" + listOfUser + ":" + earlistTweet;
			return result;
		}

	}

}


