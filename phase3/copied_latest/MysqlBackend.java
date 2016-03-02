import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import org.apache.commons.dbcp2.BasicDataSource;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class MysqlBackend extends Verticle {
	private static final String X = "8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773";
	private static final String teamID = "theImp";
	private static final String teamAWSID = "4371-1035-2488";
	private static final String user = "root";
	private static final String password = "password";
	private String teamTimeInfo = teamID + ',' + teamAWSID + '\n';
	private static final String teamInfo = "theImp, 4371-1035-2488\n";

	private final String newline = "\"15619sucksnewline\"";//customized line terminator	
	private Connection conn = null;

	// connect to mysql database
	public void Setup_connection() {

		try {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUsername(user);
			dataSource.setPassword(password);
			dataSource.setUrl("jdbc:mysql://localhost/q4_db?useUnicode=true&characterEncoding=UTF-8");
			dataSource.setMaxTotal(5);
			dataSource.setMaxIdle(0);			
			dataSource.setInitialSize(1);
			dataSource.setDefaultReadOnly(true);
			dataSource.setValidationQuery("SELECT 1");			

			conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeQuery("set character_set_connection = 'utf8mb4'");
			stmt.executeQuery("set character_set_results = 'utf8mb4'");
			stmt.executeQuery("set character_set_client = 'utf8mb4'");
			stmt.executeQuery("set character_set_server = 'utf8mb4'");
			stmt.executeQuery("SET NAMES 'utf8mb4'");
			stmt.close();
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

		routeMatcher.get("/q2", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String userid = map.get("userid");
				final String tweet_time = map.get("tweet_time");
				final String key = userid + '+' + tweet_time.replace(' ', '+');;

				String tweets = handleRequestQ2(key);
				req.response().putHeader("Content-Type",
						"text/plain; charset=UTF-8").end(tweets, "UTF-8"); 
			}
		});
		/* q3 handler */
		routeMatcher.get("/q3", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String userid = map.get("userid");
				final String start_date = map.get("start_date");
				final String end_date = map.get("end_date");
				final String n = map.get("n");

				String res = handleRequestQ3(userid, start_date, end_date, Integer.parseInt(n));
				req.response().putHeader("Content-Type", "text/plain; charset=UTF-8")
				.end(res, "UTF-8"); 

			}
		});

		routeMatcher.get("/q4", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String hashtag = map.get("hashtag");
				final String n = map.get("n");

				String res = handleRequestQ4(hashtag, Integer.parseInt(n));

				req.response().putHeader("Content-Type",
						"text/plain; charset=UTF-8").end(res, "UTF-8"); 

			}
		});

		routeMatcher.get("/q6", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String tweetid = map.get("tweetid");
				String res = handleRequestQ6(tweetid);
				req.response().putHeader("Content-Type",
						"text/plain; charset=UTF-8").end(res, "UTF-8"); 

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

	/*****************************************************************************
	 * Q2 helper methods
	 *****************************************************************************/
	public String handleRequestQ2(String key) {
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
			}
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return teamTimeInfo + tweets;
	}

	/*****************************************************************************
	 * Q3 helper methods
	 *****************************************************************************/
	/* handle request based on url from listening, formulate response string */
	public String handleRequestQ3(String userid, String start_date, String end_date, int n){
		String resPos = "";
		String resNeg = "";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// execute SELECT query
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT r FROM q3_table_pos WHERE k = " + userid);
			if (rs.next()) {
				resPos = parseGetResultQ3(rs.getString("r"), start_date, end_date, n);
			}
			rs = stmt.executeQuery("SELECT r FROM q3_table_neg WHERE k = " + userid);
			if (rs.next()) {
				resNeg = parseGetResultQ3(rs.getString("r"), start_date, end_date, n);
			}
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return teamInfo + "Positive Tweets\n" + resPos + 
				"\nNegative Tweets\n" + resNeg;
	}
	
	/* replace all customized separator and terminator with actual ones */
	public String parseGetResultQ3(String responseString, 
			String start_date, String end_date, int n) {
		
		if(responseString == null || responseString.length() < 1) {
			return "";
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
			return "";
		}
	    StringBuilder sb = new StringBuilder();
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
				return "";
			}
			//if date is in range (start and end included)
			if(currDate.equals(firstDate) || currDate.equals(secondDate) ||
					(currDate.after(firstDate) && currDate.before(secondDate))) {
				line = line.replaceAll("\\\\n", "\n").replaceAll("\\\\\\\"", "\"")
						.replaceAll("\\\\r", "\n");//.replaceAll("\\\\\\\\", "\\");
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
		String res = "";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// execute SELECT query
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT r FROM q4_table WHERE k = '" + hashtag + "' COLLATE utf8mb4_bin");
			if (rs.next()) {
				res = parseGetResultQ4(rs.getString("r"), n);
			}
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return teamTimeInfo + res;
	}

	/* replace all customized separator and terminator with actual ones */
	public String parseGetResultQ4(String responseString, int n) {
		if(responseString == null) {
			return null;
		}
		int count = 0;
		StringBuilder sb = new StringBuilder();
		String newString = responseString;
		while (newString.startsWith(newline)) {
			sb.append("\n");
			newString = newString.substring(19);
		}
		String[] lines = newString.split(newline);
		for(String line : lines) { //lines already sorted by count then by date
			if(count >= n) break;
			line = line.replaceAll("\\\\n", "\n").replaceAll("\\\\\\\"", "\"");
			sb.append(line);
			if(count != n - 1) sb.append("\n");
			count++;
		}
		while (newString.endsWith(newline)) {
			sb.append("\n");
			newString = newString.substring(0, newString.length() - 19);
		}
		return new String(sb);
	}

	/*****************************************************************************
	 * Q6 helper methods
	 *****************************************************************************/
	public String handleRequestQ6(String key) {
		String tweets = "";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// execute SELECT query
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT r FROM q6_table WHERE k='" + key + "'");
			if (rs.next()) {
				tweets = rs.getString("r")
						.replaceAll("\\\\n", "\n")
						.replaceAll("\\\\\\\"", "\"")
						.replaceAll(newline, "\n");
			}
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return teamTimeInfo + tweets;
	}

	
}
