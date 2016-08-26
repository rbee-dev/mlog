package io.rbee;

import io.rbee.system.Runner;

public class Main
{
	public static void main(String[] args) throws Exception 
	{		
		long start = System.currentTimeMillis();
		Runner runner = new Runner(args);
		runner.run();		
		
		System.out.println(System.currentTimeMillis() - start);
	}
	
	/**
	 * Client client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		
		
		SearchResponse sr = client
				.prepareSearch("zeisy_client_03")
				.setTypes("a1_operationexecutionrecord").setQuery(QueryBuilders.matchAllQuery())
				.addSort(SortBuilders.fieldSort("timestamp").order(SortOrder.ASC))				
				.setFrom(0)
				.setSize(10000)
				.setScroll(new TimeValue(60000))
				.execute()			
				.actionGet();
		
		boolean hasHits = false;
		int count = 0;
		do
		{
			
			SearchHits hits = sr.getHits();
			SearchHit[] docs = hits.getHits();
			
			System.out.println(docs.length);
			for (SearchHit hit : docs)
			{
				Map<String, Object> source = hit.getSource();
				System.out.println(source.get("c3"));
				count++;
			}
			
			sr = client.prepareSearchScroll(sr.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
			
			hasHits = !(sr.getHits().getHits().length == 0);		
		}
		while(hasHits);
		System.out.println(count);
		
		client.close();
		
		

	 */

}
