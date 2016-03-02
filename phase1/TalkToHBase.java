import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TalkToHBase {
	//private final String userid;
	//private final String tweet_time;
	//private final String searchKey;
	
	private Pattern newlinePattern;
	//private Pattern tabPattern;
	//private Pattern carriagePattern;

	private final String hbaseTableName = "tweetdata";
	private final String hbaseMasterIP = "172.31.4.177";

	private Configuration hBaseConfig;
	private HTable hBaseTable;
	
	/* initialization for regex pattern and request info 
	 * build connection with Hbase here */
	public TalkToHBase(Pattern newlinePattern) {
		//load input parameters
		//this.userid = userid;
		//this.tweet_time = tweet_time;
		//this.searchKey = userid + '+' + tweet_time;
		//compile regex for parsing result
		this.newlinePattern = newlinePattern;
		//this.tabPattern = tabPattern;
		//this.carriagePattern = carriagePattern;
		
		//initialize hbase connection, host will be localhost
		hBaseConfig = HBaseConfiguration.create(); 
		//hBaseConfig.clear();
		hBaseConfig.set("fs.hdfs.impl", "emr.hbase.fs.BlockableFileSystem");
		hBaseConfig.set("hbase.zookeeper.quorum", hbaseMasterIP);
		hBaseConfig.set("hbase.rootdir", "hdfs://" + hbaseMasterIP + ":9000/hbase");
		//increase threads to 100 since there are only GET queries
		hBaseConfig.set("hbase.client.max.total.tasks", "20000");
		hBaseConfig.set("hbase.client.max.perserver.tasks", "20000");
		hBaseConfig.set("hbase.regionserver.handler.count", "20000");
		hBaseConfig.set("hbase.zookeeper.property.maxClientCnxns", "10000");
		
		hBaseConfig.set("hbase.cluster.distributed", "true");
		hBaseConfig.set("hbase.master.wait.for.log.splitting", "false");
		
		hBaseConfig.set("hfile.block.cache.size", "0.95");
		hBaseConfig.set("hbase.hregion.max.filesize", "10737418240");
		hBaseConfig.set("hbase.hregion.memstore.flush.size", "134217728");
		hBaseConfig.set("hbase.hregion.memstore.block.multiplier", "4");
		hBaseConfig.set("hbase.hstore.blockingStoreFiles", "30");
		//hBaseConfig.set("hbase.client.scanner.caching", "100");
		
		//hBaseConfig.set("hbase.regionserver.global.memstore.size", "0.4");
		hBaseConfig.set("hbase.zookeeper.property.clientPort", "2181");
		//hBaseConfig.setBoolean("dfs.client.read.shortcircuit", true);
		try {
			hBaseTable = new HTable(hBaseConfig, hbaseTableName);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/* talk to Hbase and do Get */
	public String getResultFromHBase(String userid, String tweet_time) {
		//long startTime = System.currentTimeMillis();
		final String searchKey = userid + '+' + tweet_time;
		//GET result from the data base based on searchKey
		Get get = new Get(Bytes.toBytes(searchKey));
		Result getResult = null;
		try {
			getResult = hBaseTable.get(get);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		
		//get data from col family and qualifier, convert to string 
		if(getResult.isEmpty()) {
			//int processingTime = (int) (System.currentTimeMillis() - startTime);
			//System.out.println(Integer.toString(processingTime));
			return null;
		}
		String getResultString = new String(getResult.
				getValue(Bytes.toBytes("data"), Bytes.toBytes("response")), StandardCharsets.UTF_8);
		//int processingTime = (int) (System.currentTimeMillis() - startTime);
		//System.out.println(Integer.toString(processingTime));
		return parseGetResult(getResultString);
	}
	
	/* replace all customized separator and terminator with actual ones */
	public String parseGetResult(String getResultString) {
		if(getResultString == null) {
			return null;
		}
		getResultString = getResultString.replaceAll("\\\\n", System.getProperty("line.separator"));
		getResultString = getResultString.replaceAll("\\\\\\\"", "\"");
		getResultString = newlinePattern.matcher(getResultString).replaceAll(System.getProperty("line.separator"));
		//getResultString = tabPattern.matcher(getResultString).replaceAll("\t");
		//getResultString = carriagePattern.matcher(getResultString).replaceAll("\r");
		return getResultString;
	}
	
	public void closeHBaseConnection() {
		try {
			hBaseTable.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
