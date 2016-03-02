import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

public class q2_vertx_hbase extends Verticle {
	
	private static final String teamID = "theImp";
	private static final String teamAWSID = "4371-1035-2488";
	
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
							responseString = handleRequest(userid, tweet_time);
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
	
	/* handle request based on url from listening, formulate response string */
	public String handleRequest(String userid, String tweet_time){
		String teamInfo = teamID + ',' + teamAWSID +'\n';
		tweet_time = tweet_time.replace(' ', '+');
		String searchKey = userid + '+' + tweet_time;
		if(hashCache.containsKey(searchKey)) {
			return teamInfo + parseGetResult(hashCache.get(searchKey));
		}
		else {
			String responseString = talkToHBase.getResultFromHBase(searchKey);
			responseString = parseGetResult(responseString);
			hashCache.put(searchKey, responseString);
			return teamInfo + responseString;
		}
	}
		
	/* replace all customized separator and terminator with actual ones */
	public String parseGetResult(String responseString) {
		if(responseString == null) {
			return null;
		}
		responseString = responseString.replaceAll("\\\\n", System.getProperty("line.separator"));
		responseString = responseString.replaceAll("\\\\\\\"", "\"");
		responseString = newlinePattern.matcher(responseString).replaceAll(System.getProperty("line.separator"));
		return responseString;
	}
}
