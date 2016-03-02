import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class q1_vertx extends Verticle {

	private static final String X = "8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773";
	private static final String teamID = "theImp";
	private static final String teamAWSID = "4371-1035-2488";
		
	@Override
	public void start() {
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(65534);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(1 * 1024);

		routeMatcher.get("/q1", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				//Thread t = new Thread(new Runnable() {
					//public void run() {
						MultiMap map = req.params();
						final String key = map.get("key");
						final String message = map.get("message");
						
						String responseString = null;
						
						if(key == null || message == null) {
							responseString = "Please double check you input parameters. Request Failed";
						}
						else {
							responseString = handleRequest(key, message);
						}
						req.response().end(responseString); 
					//}
				//});
				//t.start();
				
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
	
	/* handle request based on url from listening, formulate response string */
	public String handleRequest(String key, String message){
		//set timezone to be Anguilla
		TimeZone tz = TimeZone.getTimeZone("America/Anguilla");
		SimpleDateFormat timecurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timecurr.setTimeZone(tz);
		String currentTime = timecurr.format(new Date());
		//String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String teamTimeInfo = teamID + ',' + teamAWSID + '\n' + currentTime + '\n';
		
		return teamTimeInfo + decryption(key, message) + '\n';
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
