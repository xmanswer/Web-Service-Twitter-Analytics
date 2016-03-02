import java.io.IOException;

public class q2Test {
	public static void main(String[] args) throws IOException {
		String userid = "1000836278";
		String tweet_time = "2014-05-15 11:52:54";
		TalkToHBase talkToHBase = new TalkToHBase(userid, tweet_time);
		String responseString = talkToHBase.getResultFromHBase();
		//talkToHBase.closeHBaseConnection();
		System.out.println(responseString);
	}
}
