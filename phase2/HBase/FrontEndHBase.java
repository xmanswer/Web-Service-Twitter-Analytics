import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class FrontEndHBase extends Verticle {
	private static final String X = "8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773";
	private static final String teamInfo = "theImp, 4371-1035-2488\n";
	
	private final String newline = "\"15619sucksnewline\"";//customized line terminator
	private TalkToHBase talkToHBase;
	private ConcurrentMap<String, String>  hashCache;
	private Pattern newlinePattern;
	
	@Override
	public void start() {
		hashCache = vertx.sharedData().getMap("hashCache");
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(65534);
		server.setUsePooledBuffers(true);
		server.setSendBufferSize(1 * 1024);
		server.setReceiveBufferSize(1 * 1024);
		
		newlinePattern = Pattern.compile(newline);
		talkToHBase = new TalkToHBase(newlinePattern);
		
		//final ExecutorService executorService = Executors.newFixedThreadPool(10000);
		
		/* q1 handler */
		routeMatcher.get("/q1", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
						MultiMap map = req.params();
						final String key = map.get("key");
						final String message = map.get("message");
						
						String responseString = null;
						
						if(key == null || message == null) {
							responseString = "Please double check you input parameters. Request Failed";
						}
						else {
							responseString = handleRequestQ1(key, message);
						}
						req.response().end(responseString); 
			}
		});
		
		/* q2 handler */
		routeMatcher.get("/q2", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				Thread t = new Thread(new Runnable() {
				//executorService.execute(new Runnable() {
					public void run() {
						MultiMap map = req.params();
						final String userid = map.get("userid");
						final String tweet_time = map.get("tweet_time");
						
						String responseString = null;
						
						if(userid == null || tweet_time == null) { //error handling
							responseString = "Please double check you input parameters. Request Failed";
						}
						else {
							responseString = handleRequestQ2(userid, tweet_time);
						}
						req.response().putHeader("Content-Type", "text/plain; charset=UTF-8")
							.end(responseString, "UTF-8"); 
					}
				});
				t.start();
			}
		});
		
		/* q3 handler */
		routeMatcher.get("/q3", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				Thread t = new Thread(new Runnable() {
				//executorService.execute(new Runnable() {
					public void run() {
						MultiMap map = req.params();
						final String userid = map.get("userid");
						final String start_date = map.get("start_date");
						final String end_date = map.get("end_date");
						final String n = map.get("n");
						
						String responseString = null;
						
						if(userid == null || start_date == null
								|| end_date == null || n == null) { //error handling
							responseString = "Please double check you input parameters. Request Failed";
						}
						else {
							responseString = handleRequestQ3(userid, start_date, end_date, Integer.parseInt(n));
						}
						req.response().putHeader("Content-Type", "text/plain; charset=UTF-8")
							.end(responseString, "UTF-8"); 
					}
				});
				t.start();
			}
		});
		
		/* q4 handler */
		routeMatcher.get("/q4", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				Thread t = new Thread(new Runnable() {
				//executorService.execute(new Runnable() {
					public void run() {
						MultiMap map = req.params();
						final String hashtag = map.get("hashtag");
						final String n = map.get("n");
						
						String responseString = null;
						
						if(hashtag == null || n == null) { //error handling
							responseString = "Please double check you input parameters. Request Failed";
						}
						else {
							responseString = handleRequestQ4(hashtag, Integer.parseInt(n));
						}
						req.response().putHeader("Content-Type", "text/plain; charset=UTF-8")
							.end(responseString, "UTF-8"); 
					}
				});
				t.start();
			}
		});
		
		routeMatcher.noMatch(new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().end();
			}
		});
		
		server.requestHandler(routeMatcher);
		server.listen(80);
	}
	
	@Override
	public void stop() {
		talkToHBase.closeHBaseConnection();
	}

	/*****************************************************************************
	 * Q1 helper methods
	 *****************************************************************************/
	/* handle request based on url from listening, formulate response string */
	public String handleRequestQ1(String key, String message){
		//set timezone to be Anguilla
		TimeZone tz = TimeZone.getTimeZone("America/Anguilla");
		SimpleDateFormat timecurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timecurr.setTimeZone(tz);
		String currentTime = timecurr.format(new Date());
		
		return teamInfo + currentTime + '\n' + decryption(key, message) + '\n';
	}
	
	/* based on key and encoded msg, decode it and return the real msg */
	public String decryption(String key, String encodedMsgC) {
		BigInteger bigIntegerX = new BigInteger(X);
		BigInteger bigIntegerKey = new BigInteger(key);
		BigInteger Y = bigIntegerKey.divide(bigIntegerX);
		int Z = 1 + Y.mod(new BigInteger("25")).intValue();
		int n = (int)Math.sqrt(encodedMsgC.length()); //msg C is str of length n x n 
		
		//do diagonalization to get index list of intermediate msg I
		LinkedList<Integer> indexList = new LinkedList<Integer>();
		for(int i = 1; i <= n; i++) {
			for(int j = 0; j < i; j++) {
				indexList.add(i + j * (n - 1));
			}
		}
		for(int i = 2; i <= n; i++) {
			for(int j = 0; j < n - i + 1; j++) {
				indexList.add(i * n + j * (n - 1));
			}
		}
		
		//loop through M based on index list and shift char back based on Z
		StringBuilder M = new StringBuilder(encodedMsgC.length());
		for(int i : indexList) {
			char asciiChar = (char)(encodedMsgC.charAt(i - 1) - Z);
			if(asciiChar < 'A') {
				asciiChar = (char)(asciiChar + 26);
			}
			M.append(asciiChar);
		}
		return M.toString();
	}

	/*****************************************************************************
	 * Q2 helper methods
	 *****************************************************************************/
	/* handle request based on url from listening, formulate response string */
	public String handleRequestQ2(String userid, String tweet_time){
		tweet_time = tweet_time.replace(' ', '+');
		String searchKey = userid + '+' + tweet_time;
		if(hashCache.containsKey(searchKey)) {
			return  hashCache.get(searchKey);
		}
		else {
			String responseString = talkToHBase.getResultFromHBase(searchKey, 0);
			responseString = teamInfo + parseGetResultQ2(responseString);
			hashCache.put(searchKey, responseString);
			return responseString;
		}
	}
		
	/* replace all customized separator and terminator with actual ones */
	public String parseGetResultQ2(String responseString) {
		if(responseString == null) {
			return null;
		}
		responseString = responseString.replaceAll("\\\\n", "\n");
		responseString = responseString.replaceAll("\\\\\\\"", "\"");
		responseString = newlinePattern.matcher(responseString).replaceAll("\n");
		return responseString;
	}
	
	/*****************************************************************************
	 * Q3 helper methods
	 *****************************************************************************/
	/* handle request based on url from listening, formulate response string */
	public String handleRequestQ3(String userid, 
			String start_date, String end_date, int n){
		if(hashCache.containsKey(userid)) { //userid is key
			return hashCache.get(userid);
		}
		else {
			String responseStringPos = talkToHBase.getResultFromHBase(userid, 1);
			String responseStringNeg = talkToHBase.getResultFromHBase(userid, 2);
			responseStringPos = parseGetResultQ3(responseStringPos, start_date, end_date, n);
			responseStringNeg = parseGetResultQ3(responseStringNeg, start_date, end_date, n);
			String responseString = "Positive Tweets\n" + responseStringPos + 
					"\nNegative Tweets\n" + responseStringNeg;
			hashCache.put(userid, responseString);
			return  responseString;
		}
	}
		
	/* replace all customized separator and terminator with actual ones */
	public String parseGetResultQ3(String responseString, 
			String start_date, String end_date, int n) {
		
		if(responseString == null) {
			return null;
		}
		Date firstDate = null;
		Date secondDate = null;
		//parse string into date
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    try {
			firstDate = dateFormat.parse(start_date);
			secondDate = dateFormat.parse(end_date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	    StringBuilder sb = new StringBuilder(teamInfo);
		String[] lines = responseString.split(newline);
		int count = 0;
		for(String line : lines) { //lines already sorted by score then tweet id
			if(count >= n) break; //if already found n tweets
			int comma = line.indexOf(',');
			String date = line.substring(0, comma-1); //first element is date
			Date currDate = null;
			try {
				currDate = dateFormat.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
			//if date is in range (start and end included)
			if(currDate.equals(firstDate) || currDate.equals(secondDate) ||
					(currDate.after(firstDate) && currDate.before(secondDate))) {
				line = line.replaceAll("\\\\n", "\n").replaceAll("\\\\\\\"", "\"");
				sb.append(line).append("\n");
				count++;
			}
		}
		return new String(sb);
	}
	
	/*****************************************************************************
	 * Q4 helper methods
	 *****************************************************************************/
	/* handle request based on url from listening, formulate response string */
	public String handleRequestQ4(String hashtag, int n){
		if(hashCache.containsKey(hashtag)) { //hashtag is key
			return hashCache.get(hashtag);
		}
		else {
			String responseString = talkToHBase.getResultFromHBase(hashtag, 3);
			responseString = parseGetResultQ4(responseString, n);
			hashCache.put(hashtag, responseString);
			return responseString;
		}
	}
		
	/* replace all customized separator and terminator with actual ones */
	public String parseGetResultQ4(String responseString, int n) {
		if(responseString == null) {
			return null;
		}
		String[] lines = responseString.split(newline);
		int count = 0;
		StringBuilder sb = new StringBuilder(teamInfo);
		for(String line : lines) { //lines already sorted by count then by date
			if(count >= n) break;
			line = line.replaceAll("\\\\n", "\n").replaceAll("\\\\\\\"", "\"");
			sb.append(line).append("\n");
			count++;
		}
		return new String(sb);
	}
}
