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

	private Pattern newlinePattern;

	private final String q2TableName = "q2";
	private final String q3TableName = "q3";
	private final String q4TableName = "q4";
	private final String hbaseMasterIP = "172.31.52.1";

	private Configuration hBaseConfig;
	private HTable q2Table;
	private HTable q3Table;
	private HTable q4Table;
	
	
	/* initialization for regex pattern
	 * build connection with Hbase here */
	public TalkToHBase(Pattern newlinePattern) {
		this.newlinePattern = newlinePattern;
		
		//initialize hbase connection, configure hbase
		hBaseConfig = HBaseConfiguration.create(); 
		//hBaseConfig.clear();
		//hBaseConfig.set("fs.hdfs.impl", "emr.hbase.fs.BlockableFileSystem");
		hBaseConfig.set("hbase.zookeeper.quorum", hbaseMasterIP);
		hBaseConfig.set("hbase.rootdir", "hdfs://" + hbaseMasterIP + ":9000/hbase");
		//increase threads to 100 since there are only GET queries
		hBaseConfig.set("hbase.client.max.total.tasks", "1000");
		hBaseConfig.set("hbase.client.max.perserver.tasks", "1000");
		hBaseConfig.set("hbase.client.max.perregion.tasks", "1000");
		hBaseConfig.set("hbase.regionserver.handler.count", "1000");
		hBaseConfig.set("hbase.zookeeper.property.maxClientCnxns", "10000");
		//hBaseConfig.set("hbase.ipc.server.callqueue.read.ratio", "1");
		hBaseConfig.set("hbase.hregion.majorcompaction", "0");
		//hBaseConfig.set("hbase.cluster.distributed", "true");
		hBaseConfig.set("hbase.master.wait.for.log.splitting", "false");
		hBaseConfig.set("hbase.master.distributed.log.splitting", "false");
		hBaseConfig.set("hfile.block.cache.size", "0.6");
		//hBaseConfig.set("zookeeper.session.timeout", "20000");
		hBaseConfig.set("hbase.hregion.max.filesize", "107374182400");
		//hBaseConfig.set("hbase.hregion.memstore.flush.size", "134217728");
		//hBaseConfig.set("hbase.hregion.memstore.block.multiplier", "4");
		//hBaseConfig.set("hbase.hstore.blockingStoreFiles", "30");
		//hBaseConfig.set("hbase.client.scanner.caching", "100");
		
		//hBaseConfig.set("hbase.rest.readonly", "true");
		//hBaseConfig.set("hbase.rest.threads.min", "100");
		//hBaseConfig.set("hbase.rest.threads.max", "1000");
		//hBaseConfig.set("hbase.regionserver.global.memstore.size", "0.4");
		hBaseConfig.set("hbase.zookeeper.property.clientPort", "2181");
		//hBaseConfig.setBoolean("dfs.client.read.shortcircuit", true);
		
		
		
		try {
			q2Table = new HTable(hBaseConfig, q2TableName);
			q3Table = new HTable(hBaseConfig, q3TableName);
			q4Table = new HTable(hBaseConfig, q4TableName);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/* talk to Hbase and do Get for q2 and q4, tableIndex 0 for q2, 3 for q4 */
	public String getResultFromHBase(String searchKey, int tableIndex) {
		//GET result from the data base based on searchKey
		Get get = new Get(Bytes.toBytes(searchKey));
		Result getResult = null;
		try {
			if(tableIndex == 0)
				getResult = q2Table.get(get);
			else if(tableIndex == 3)
				getResult = q4Table.get(get);
			else
				getResult = null;
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		//get data from col family and qualifier, convert to string 
		if(getResult.isEmpty()) {
			return null;
		}
		String getResultString = new String(getResult.
				getValue(Bytes.toBytes("data"), Bytes.toBytes("response")), StandardCharsets.UTF_8);
		return getResultString;
	}
	
	/* talk to Hbase and do Get, tableIndex 0 for q2, 1 for q3pos, 
	 * 2 for q3neg, 3 for q4 */
	public Result getResultFromHBaseQ3(String searchKey) {
		//GET result from the data base based on searchKey
		Get get = new Get(Bytes.toBytes(searchKey));
		Result getResult = null;
		try {
			getResult = q3Table.get(get);
		} catch(IOException e) {
			e.printStackTrace();
		}
		 return getResult;
	}

	/*close connection with hbase table */
	public void closeHBaseConnection() {
		try {
			q2Table.close();
			q3Table.close();
			q4Table.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
