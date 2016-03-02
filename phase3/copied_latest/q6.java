import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.*;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;

public class q6 extends Verticle {
	private static final String[] backend = new String[8];
	// team information
	private static final String teamID = "theImp";
	private static final String teamAWSID = "4371-1035-2488";
	private String teamTimeInfo = teamID + ',' + teamAWSID + '\n';
	
	private void initBackendDNS() {
		backend[0] = "ec2-54-175-137-112.compute-1.amazonaws.com";
		backend[1] = "ec2-54-84-253-201.compute-1.amazonaws.com";
		backend[2] = "ec2-52-91-90-230.compute-1.amazonaws.com";
		backend[3] = "ec2-54-164-114-140.compute-1.amazonaws.com";
		backend[4] = "ec2-54-88-196-20.compute-1.amazonaws.com";
		backend[5] = "ec2-52-90-232-85.compute-1.amazonaws.com";
		backend[6] = "ec2-54-164-152-241.compute-1.amazonaws.com";
		backend[7] = "ec2-54-88-179-55.compute-1.amazonaws.com";
	}

	
	// tid --> (seq --> req)
	private static HashMap<String, TreeMap<Integer, HttpServerRequest>> reqs
		= new HashMap<String, TreeMap<Integer, HttpServerRequest>>();

	// tid --> seq 
	private static HashMap<String, Integer> done_seq
		= new HashMap<String, Integer>();	

	// tweetid --> tag
	private static HashMap<String, String> tags= new HashMap<String, String>();
	
	@Override
	public void start() {
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setSendBufferSize(4 * 1024);
		server.setReceiveBufferSize(4 * 1024);
		
		initBackendDNS();
		final HttpClient[] httpClient = new HttpClient[8];
		for(int i = 0; i < httpClient.length; i++) {
			httpClient[i] = vertx.createHttpClient()
					.setHost(backend[i])
					.setPort(80);
		}
		
		//final ExecutorService executorService = Executors.newFixedThreadPool(10);

		/* q6 handler */
		routeMatcher.get("/q6", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
		//executorService.execute(new Runnable() {
		//Thread t = new Thread(new Runnable() {
			//public void run() {
				MultiMap map = req.params();
				final String tid = map.get("tid");
				final String opt = map.get("opt");
				if (!reqs.containsKey(tid)) {
					// initialize data structure
					TreeMap<Integer, HttpServerRequest> tmp = new TreeMap<Integer, HttpServerRequest>();
					reqs.put(tid, tmp);
					done_seq.put(tid, new Integer(0));
				}
				if (opt.equals("s") || opt.equals("e")) {
					// handle start & end
					req.response().putHeader("Content-Type",
					"text/plain; charset=UTF-8").end(teamTimeInfo + "0\n", "UTF-8");
					return;
				}
				final Integer seq = Integer.valueOf(map.get("seq"));
				//final String tweetid = map.get("tweetid");
				reqs.get(tid).put(seq, req);
				if (opt.equals("a")) {
					final String tag = map.get("tag");
					req.response().putHeader("Content-Type",
					"text/plain; charset=UTF-8").end(teamTimeInfo + tag + "\n", "UTF-8");
				}
				if (reqs.get(tid).headMap(seq, true).size() != seq.intValue())
					return;
				else {
					Iterator<HttpServerRequest> it = 
						reqs.get(tid).subMap(done_seq.get(tid), false, seq, true).values().iterator();
					while(it.hasNext()) {
						final HttpServerRequest r = it.next();
						MultiMap r_map = r.params();
						final String r_opt = r_map.get("opt");
						final String r_tweetid = r_map.get("tweetid");
						final Integer r_seq = Integer.valueOf(r_map.get("seq"));
						if (r_opt.equals("a")) {
							// store the latest tag
							tags.put(r_tweetid, r_map.get("tag"));
						}
						else {
							String r_tag = tags.get(r_tweetid);
							int hashIndex = hashFunction(r_tweetid);
							final HttpClient thisHttpClient =  httpClient[hashIndex-1];
							handleRequest(r, thisHttpClient, r_tag);
						}
						done_seq.put(tid, r_seq);
					}
					if (reqs.get(tid).size() == 5) {
						done_seq.remove(tid);
						reqs.remove(tid);
					}
				}
			//}
		//});
		//t.start();

			}
		});

		routeMatcher.noMatch(new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().putHeader("Content-Type",
							"text/plain; charset=UTF-8").end("Not found.");
			}
		});
		server.requestHandler(routeMatcher);
		server.listen(49153);
	}

	private void handleRequest(final HttpServerRequest req, final HttpClient httpClient, final String tag) {

		httpClient.getNow(req.uri(), 
				new Handler<HttpClientResponse>() {
			@Override
			public void handle(HttpClientResponse httpClientResponse) {

				httpClientResponse.bodyHandler(new Handler<Buffer>() {
					@Override
					public void handle(Buffer buffer) {
						if (tag == null)
							req.response().end(buffer.appendString("\n")); 
						else
							req.response().end(buffer.appendString(tag).appendString("\n")); 
					}
				});
			}
		});
	}

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
}
