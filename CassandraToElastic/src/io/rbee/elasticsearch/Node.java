package io.rbee.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Connection Class which wraps the Elasticsearch API
 * @author Sven Ulrich
 *
 */
public class Node
{
	/** Fields **/
	private Client	 client;
	private String	 ip;
	private int		 port = ELASTIC_DEFAULT_PORT;
	
	public static final int ELASTIC_DEFAULT_PORT = 9300;
	
	/** Constructor **/
	
	/**
	 * Creates a new instance of this class
	 */
	public Node()
	{
		/** EMPTY **/
	}
	
	/**
	 * Creates a new instance of this class
	 * @param ip
	 */
	public Node(String ip)
	{
		setIp(ip);
		connect();
	}
	
	/**
	 * Creates a new instance of this class
	 * @param ip
	 * @param port
	 */
	public Node(String ip, int port)
	{
		setIp(ip);
		setPort(port);
		connect();
	}
	
	/**
	 * Creates a BulkProcessor with default settings
	 * @return
	 */
	private BulkProcessor getBulkProcessor()
	{
		return getBulkProcessor(1000); //default value of elastic api
	}
	
	/**
	 * Creates a BulkProcessor
	 * @param bulkSize
	 * @return
	 */
	private BulkProcessor getBulkProcessor(int bulkSize)
	{
		BulkProcessor bulkProcessor = BulkProcessor.builder(client, new Listener() {
			
			@Override
			public void beforeBulk(long executionId, BulkRequest request)
			{
				System.out.println("Sending bulk with " + request.numberOfActions() + " actions");
				
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable error)
			{
				System.out.println("Error sending bulk: " + error.getMessage());
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response)
			{
				System.out.println("Bulk send with " + request.numberOfActions() + " actions");				
			}
		})
		.setBulkActions(bulkSize)
		.setConcurrentRequests(2)
		.build();
		
		return bulkProcessor;
	}
	
	/** Setter **/
	
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}

	/** Getter **/
	
	public String getIp()
	{
		return ip;
	}
	
	public int getPort()
	{
		return port;
	}

	/**
	 * Connects to the Elastic Cluster
	 */
	@SuppressWarnings("resource")
	public void connect()
	{
		if (this.ip != null && !this.ip.isEmpty())
		{
			try
			{								
				//this.client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(this.ip), this.port));
				this.client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(this.ip), this.port));
				
			}
			catch (UnknownHostException e)
			{			
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends an index request to the Elastic Cluster
	 * @param index
	 * @param type
	 * @param id
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public boolean index(String index, String type, String id, Map<String, Object> value) throws Exception
	{
		boolean result = false; 
		if (this.client == null)
		{
			throw new Exception("Connection not established, please use connect to a connection");
		}
		IndexResponse response = client.prepareIndex(index, type, id).setSource(value).get();
		if (response.getId() != null && !response.getId().isEmpty())
		{
			result = true;
		}
		return result;
		//return response.isCreated(); not available in API 5.0 despite being part of the official documentation
	}
	
	/**
	 * Sends an bulk index request to the Elastic Cluster
	 * @param index
	 * @param type
	 * @param data
	 * @param bulkSize
	 * @throws Exception
	 */
	public void bulkIndex(String index, String type, List<Map<String,Object>> data, int bulkSize) throws Exception
	{
		if (this.client == null)
		{
			throw new Exception("Connection not established, please use connect to a connection");
		}
		BulkProcessor bulkProcessor = getBulkProcessor(bulkSize);
		
		for (Map<String, Object> value : data)
		{									
			bulkProcessor.add(new IndexRequest(index, type).source(value));
		}
		
		bulkProcessor.close();
	}
	
	/**
	 * Sends an bulk index request to the Elastic Cluster
	 * @param index
	 * @param type
	 * @param data
	 * @throws Exception
	 */
	public void bulkIndex(String index, String type, List<Map<String,Object>> data) throws Exception
	{
		if (this.client == null)
		{
			throw new Exception("Connection not established, please use connect to a connection");
		}
		BulkProcessor bulkProcessor = getBulkProcessor();
		
		for (Map<String, Object> value : data)
		{			
			bulkProcessor.add(new IndexRequest(index, type).source(value));		
		}
		
		bulkProcessor.close();
	}
	
	/**
	 * Closes the connection to the Elastic Cluster
	 */
	public void close()
	{
		if (this.client != null)
		{
			this.client.close();
		}
	}
}
