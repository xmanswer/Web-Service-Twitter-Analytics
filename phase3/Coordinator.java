import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;


public class Coordinator extends Verticle {
	private static final String[] backend = new String[8];
	private int roundRobin = 0;
	private static final String teamInfo = "theImp, 4371-1035-2488\n";
	private static final String X = "8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773";
	private String teamTimeInfo = teamInfo;
	
	private void initBackendDNS() {
		backend[0] = "ec2-54-210-102-148.compute-1.amazonaws.com";
		backend[1] = "ec2-54-210-103-39.compute-1.amazonaws.com";
		backend[2] = "ec2-52-91-61-60.compute-1.amazonaws.com";
		backend[3] = "ec2-54-175-62-26.compute-1.amazonaws.com";
		backend[4] = "ec2-54-209-140-148.compute-1.amazonaws.com";
		backend[5] = "ec2-54-210-171-37.compute-1.amazonaws.com";
		backend[6] = "ec2-54-210-171-22.compute-1.amazonaws.com";
		backend[7] = "ec2-52-91-14-135.compute-1.amazonaws.com";
	}

	@Override
	public void start() {
		initBackendDNS();
		
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(65534);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);
		server.setSendBufferSize(4 * 1024);

		final HttpClient[] httpClient = new HttpClient[8];
		for(int i = 0; i < httpClient.length; i++) {
			httpClient[i] = vertx.createHttpClient()
					.setHost(backend[i])
					.setPort(80);
		}
		
		final HttpClient httpClientQ5 = vertx.createHttpClient()
			.setHost("localhost")
			.setPort(49152);

		//final ExecutorService executorService = Executors.newFixedThreadPool(4);
		routeMatcher.get("/q1", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				//final HttpClient thisHttpClient =  httpClient[roundRobin];
				//executorService.execute(new Runnable() {
				//public void run() {
				//handleRequest(req, thisHttpClient);
				//}
				//});
				//roundRobin = (roundRobin < 4) ? (roundRobin++) : (roundRobin = 0);
				if(roundRobin == 0) {
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
				else {
					final HttpClient thisHttpClient =  httpClient[roundRobin-1];
					handleRequest(req, thisHttpClient);
					roundRobin = (roundRobin < 2) ? (roundRobin++) : (roundRobin = 0);
				}
			}
		});

		routeMatcher.get("/q2", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String userid = map.get("userid");
				final String tweet_time = map.get("tweet_time");
				final String key = userid + '+' + tweet_time.replace(' ', '+');
				int hashIndex = hashFunction(key);
				final HttpClient thisHttpClient =  httpClient[hashIndex-1];
				handleRequest(req, thisHttpClient);
			}
		});
		
		routeMatcher.get("/q3", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String userid = map.get("userid");
				int hashIndex = hashFunction(userid);
				final HttpClient thisHttpClient =  httpClient[hashIndex-1];
				handleRequest(req, thisHttpClient);
			}
		});

		routeMatcher.get("/q4", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String hashtag = map.get("hashtag");
				int hashIndex = hashFunction(hashtag);
				final HttpClient thisHttpClient =  httpClient[hashIndex-1];
				handleRequest(req, thisHttpClient);
			}
		});
		
		/* q5 handler */
		routeMatcher.get("/q5", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				final HttpClient thisHttpClient =  httpClientQ5;
				handleRequest(req, thisHttpClient);
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

	private void handleRequest(final HttpServerRequest req, final HttpClient httpClient) {

		httpClient.getNow(req.uri(), 
				new Handler<HttpClientResponse>() {
			@Override
			public void handle(HttpClientResponse httpClientResponse) {

				httpClientResponse.bodyHandler(new Handler<Buffer>() {
					@Override
					public void handle(Buffer buffer) {
						req.response().end(buffer.toString()); 
					}
				});
			}
		});
	}
	/*****************************************************************************
	 * hash functions
	 *****************************************************************************/
	private int hashFunction(String key) {
		if(key == null) return -1;
		int len = key.length();
		int i = 0;
		long sum = 0;
		char[] chars = key.toCharArray();
		// add the ascii value of each character in the string
		while (i < len) {
			sum += ((long)(chars[i]));
			i++;
		}
		// mod the sum by 8
		int j = (int)((sum % 8) + 1);
		return j;
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
		String decryptedMsg = decryption(key, message);
		return teamInfo + currentTime + '\n' + decryptedMsg + '\n';
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
}
