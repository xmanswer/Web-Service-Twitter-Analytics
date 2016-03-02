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
import java.util.TreeMap;

import org.apache.commons.dbcp2.BasicDataSource;
//import org.apache.commons.dbcp.BasicDataSource;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class q2 extends Verticle {
	private static final String teamID = "theImp";
	private static final String teamAWSID = "4371-1035-2488";
	private static final String user = "root";
	private static final String password = "password";
	private String teamTimeInfo = teamID + ',' + teamAWSID + '\n';
	private static final String teamInfo = "theImp, 4371-1035-2488\n";

	private final String newline = "\"15619sucksnewline\"";//customized line terminator	
	private Connection conn = null;

	//	HashMap<String, String> hashCache = new HashMap<String, String>(5000000, 1);

	// connect to mysql database
	public void Setup_connection() {

		try {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUsername(user);
			dataSource.setPassword(password);
			dataSource.setUrl("jdbc:mysql://localhost/q2_db?useUnicode=true&characterEncoding=UTF-8");
			dataSource.setMaxTotal(1);
			dataSource.setMaxIdle(0);			
			dataSource.setInitialSize(1);
			dataSource.setValidationQuery("SELECT 1");			

			conn = dataSource.getConnection();
			// establish local mysql connection
			//conn = DriverManager.getConnection("jdbc:mysql://localhost/q4_db", user, password);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
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

		// establish mysql connection
		Setup_connection();

		routeMatcher.get("/q2", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String userid = map.get("userid");
				final String tweet_time = map.get("tweet_time");
				String key = userid + ' ' + tweet_time;
				/*if (ht.containsKey(key)) {
					// there is no put operation on hashmap, so no need to lock
					String tweets = ht.get(key);
					req.response().putHeader("Content-Type", "text/plain; charset=UTF-8").end(teamTimeInfo + tweets, "UTF-8");
				}
				else {*/
					Thread t = new Thread(new Runnable() {
						public void run() {
							String tweets = get_tweets(userid, tweet_time);
							req.response().putHeader("Content-Type",
									"text/plain; charset=UTF-8").end(tweets, "UTF-8"); 
						}
					});
					t.start();
				//}
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

	public String get_tweets(String userid, String tweet_time) {
		String key = userid + '+' + tweet_time.replace(' ', '+');
		String tweets = "";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// execute SELECT query
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT r FROM q2_table WHERE k='" + key + "'");
			if (rs.next()) {
				tweets = rs.getString("r")
						.replaceAll("\\\\n", "\n")
						.replaceAll("\\\\\\\"", "\"")
						.replaceAll(newline, "\n");
				// accumulate cache by piping the stdout to a file called "cache"
				// eg. sudo ./vertx run mysql.java >> cache
			}
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

		return teamTimeInfo + tweets;
	}
}


