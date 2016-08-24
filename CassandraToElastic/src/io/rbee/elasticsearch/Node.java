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
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class Node
{
	private Client client;
	private String ip;
	
	public Node()
	{
		/** EMPTY **/
	}
	
	public Node(String ip)
	{
		setIp(ip);
		connect();
	}
	
	private BulkProcessor getBulkProcessor()
	{
		return getBulkProcessor(1000); //default value of elastic api
	}
	
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
		.setConcurrentRequests(1)
		.build();
		
		return bulkProcessor;
	}
	
	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getIp()
	{
		return ip;
	}
	
	public void connect()
	{
		if (ip != null && !ip.isEmpty())
		{
			try
			{
				this.client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), 9300));
			}
			catch (UnknownHostException e)
			{			
				e.printStackTrace();
			}
		}
	}
	
	public boolean index(String index, String type, String id, Map<String, Object> value) throws Exception
	{
		if (client == null)
		{
			throw new Exception("Connection not established, please use connect to a connection");
		}
		IndexResponse response = client.prepareIndex(index, type, id).setSource(value).get();
		return response.isCreated();
	}
	
	public void bulkIndex(String index, String type, List<Map<String,Object>> data, int bulkSize) throws Exception
	{
		if (client == null)
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
	
	public void bulkIndex(String index, String type, List<Map<String,Object>> data) throws Exception
	{
		if (client == null)
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
	
	public void close()
	{
		if (client != null)
		{
			client.close();
		}
	}
}
