import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class q5 extends Verticle {
	// team information
	private static final String teamID = "theImp";
	private static final String teamAWSID = "4371-1035-2488";
	private String teamTimeInfo = teamID + ',' + teamAWSID + '\n';

	// number of lines in q5 dataset
	public final static int len = 53767998;
	// the list of keys
	public static long[] userids = new long[len];
	// the list of values
	public static int[] counts = new int[len];

	// load sorted q5 dataset to in-memory array
	public void load_q5_to_table() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("/home/ubuntu/sorted_q5_data"));
			String inputLine;
			int i = 0;
			while((inputLine=br.readLine()) != null) {
				String[] pair = inputLine.split("\t");
				// reject malformed lines
				if (pair.length != 2 ||
				    pair[0].length() == 0 ||
				    pair[1].length() == 0)
					continue;
				// key is parsed as long
				q5.userids[i] = Long.parseLong(pair[0]);
				// value is parsed as int
				q5.counts[i] = Integer.parseInt(pair[1]);
				i += 1;
				if (i % 5000000 == 0)
					System.out.println(i);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		// create the increasing sum array
		for (int i = 1; i < len; i++) {
			q5.counts[i] = q5.counts[i] + q5.counts[i-1];
		}
	}

	// get the adjusted index in values array
	private int get_index(long key) {
		// the min key is 12, adjust index to first value
		if (key < 12L)
			return 0;
		// the max key is 2594997268, adjust index to last value
		else if (key > 2594997268L)
			return len - 1;
		else {
		// find the insertion point of the values array
			int i = Arrays.binarySearch(userids, key);
			if (i >= 0)
			// found the exact value
				return i;
			else
			// adjust the insertion point if no exact value found
				return (-1 * i) - 2;
		}
	}
    
	@Override
	public void start() {
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setSendBufferSize(4 * 1024);
		server.setReceiveBufferSize(4 * 1024);
        
		// load q5 dataset
		load_q5_to_table();
		
		/* q5 handler */
		routeMatcher.get("/q5", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				long userid_min = Long.parseLong(map.get("userid_min"));
				long userid_max = Long.parseLong(map.get("userid_max"));
				// low and high bounds are inclusive, so get the accumulated sum from values array
				int sum = q5.counts[get_index(userid_max)] - q5.counts[get_index(userid_min - 1L)];
				req.response().putHeader("Content-Type",
                    "text/plain; charset=UTF-8").end(teamTimeInfo + Integer.toString(sum) + "\n", "UTF-8"); 
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
		server.listen(49152);
	}
}
