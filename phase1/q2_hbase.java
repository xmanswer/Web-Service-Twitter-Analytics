import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class q2
 */
@WebServlet("/q2")
//@WebServlet(name="q2",urlPatterns={"/q2"})
public class q2_hbase extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String teamID = "theImp";
	private static final String teamAWSID = "4371-1035-2488";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    //public q2() {
        //super();
        // TODO Auto-generated constructor stub
    //}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String query = request.getQueryString();
		if(query == null)
			response.getWriter().println("");
		else
			response.getWriter().println(handleRequest(query));
	}
	
	/* handle request based on url from listening, formulate response string */
	public static String handleRequest(String query){
		HashMap<String, String> queryMap = new HashMap<String, String>();
		
		try { //parse the url query into a parameters:values map
			String[] queryInputs = query.split("&");
			
			for(String str : queryInputs) {
				String[] paraValue = str.split("=");
				queryMap.put(paraValue[0], paraValue[1]);
			}
		} catch (NullPointerException ne) {
			return "Please double check you input parameters. Request Failed";
		} catch (ArrayIndexOutOfBoundsException ae) {
			return "Please double check you input parameters. Request Failed";
		}
		
		//String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String teamInfo = teamID + ',' + teamAWSID + '\n';
		
		//only perform decode if both key and message have non-null values
		if(queryMap.containsKey("userid") && queryMap.containsKey("tweet_time")) {
			String userid = queryMap.get("userid");
			String tweet_time = queryMap.get("tweet_time");
			if(userid != null && tweet_time != null) {
				TalkToHBase talkToHBase = new TalkToHBase(userid, tweet_time);
				String responseString = talkToHBase.getResultFromHBase();
				talkToHBase.closeHBaseConnection();
				return teamInfo + responseString;
			}
		}
		
		return "Please double check you input parameters. Request Failed";
	}
}
