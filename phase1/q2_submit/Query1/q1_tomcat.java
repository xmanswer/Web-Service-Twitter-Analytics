package net.codejava;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class q1
 */
@WebServlet("/q1")
public class q1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String X = "8271997208960872478735181815578166723519929177896558845922250595511921395049126920528021164569045773";
	private static final String teamID = "theImp";
	private static final String teamAWSID = "4371-1035-2488";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    //public q1() {
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
		
		//set timezone to be new york
		TimeZone tz = TimeZone.getTimeZone("America/Anguilla");
		SimpleDateFormat timecurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timecurr.setTimeZone(tz);
		String currentTime = timecurr.format(new Date());
		//String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String teamTimeInfo = teamID + ',' + teamAWSID + '\n' + currentTime + '\n';
		
		//only perform decode if both key and message have non-null values
		if(queryMap.containsKey("key") && queryMap.containsKey("message")) {
			String key = queryMap.get("key");
			String message = queryMap.get("message");
			if(key != null && message != null) {
				return teamTimeInfo + decryption(key, message) + "\n";
			}
		}
		
		return "Please double check you input parameters. Request Failed";
	}
	
	/* based on key and encoded msg, decode it and return the real msg */
	public static String decryption(String key, String encodedMsgC) {
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
