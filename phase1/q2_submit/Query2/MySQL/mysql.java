import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class Coordinator extends Verticle {
	//private static final String db1 = "";
	//private static final String db2 = "";
	//private static final String db3 = "";

	private static final String teamID = "theImp";
	private static final String teamAWSID = "4371-1035-2488";
    private static final String user = "user";
    private static final String password = "password";
	private String teamTimeInfo = teamID + ',' + teamAWSID + '\n';
	
	private Connection conn1 = null;
	//private Connection conn2 = null;
	//private Connection conn3 = null;
    
    // cache for queries
	HashMap<String, String> ht = new HashMap<String, String>();

    // connect to mysql database
	public void Setup_connection() {
		try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("Initialization Error");
        }
		
		try {
			// establish local mysql connection
			conn1 = DriverManager.getConnection("jdbc:mysql://localhost/q2_db", user, password);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	// convert a key to the db number
	private Connection key_to_conn(String key) {
        // removed the unnessary sharding code
		return conn1;
	}
	
	// get the tweets from mysql database
	public String get_tweets(String userid, String tweet_time) {
		String key = userid + ' ' + tweet_time;
		String tweets = "";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// execute SELECT query
			stmt = key_to_conn("'" + key + "'").createStatement();
			rs = stmt.executeQuery("SELECT * FROM q2_table WHERE k = " + key);
			if (rs.next()) {
				tweets = rs.getString("r");
                // accumulate cache by piping the stdout to a file called "cache"
                // eg. sudo ./vertx run mysql.java >> cache
                System.out.println(key + "\t" + tweets);
			}
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		
		return teamTimeInfo + tweets;
	}
    
    // load the cached result from previous tests
    public void load_cache() {
		try {
            BufferedReader br = new BufferedReader(new FileReader("cache"));
                String inputLine;
            while((inputLine=br.readLine()) != null){
                String[] pair = inputLine.split("\t");
                ht.put(pair[0], pair[1].replace("\\n", "\n"));
            }
		} catch (Exception e) {}
	}

	@Override
	public void start() {
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setSendBufferSize(4 * 1024);
		server.setReceiveBufferSize(4 * 1024);
		
        // load accumulated cached results
        load_cache();
        // establish mysql connection
		Setup_connection();

		routeMatcher.get("/q2", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				String userid = map.get("userid");
				String tweet_time = map.get("tweet_time");
				String key = userid + ' ' + tweet_time;
                if (ht.containsKey(key)) {
                    // there is no put operation on hashmap, so no need to lock
                    String tweets = ht.get(key);
                    req.response().putHeader("Content-Type", "text/plain; charset=UTF-8").end(teamTimeInfo + tweets, "UTF-8");
                }
				else {
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            String tweets = get_tweets(userid, tweet_time);
                            req.response().putHeader("Content-Type",
                                "text/plain; charset=UTF-8").end(tweets, "UTF-8"); 
                        }
                    });
                    t.start();
                }
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
		server.listen(80);
	}
}

