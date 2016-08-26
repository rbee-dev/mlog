package io.rbee.system;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import io.rbee.cassandra.Column;
import io.rbee.cassandra.Database;
import io.rbee.cassandra.Table;
import io.rbee.elasticsearch.Node;
import io.rbee.helper.Transformer;

/**
 * Class which performs the transfer between the Cassndra Cluster and the Elastic Cluster
 * @author Sven Ulrich
 *
 */
public class Runner
{
	/** Fields **/
	
	private CommandLine line;
	private boolean		debug;
	
	/** Constructor **/
	
	/**
	 * Creates a new instance of this class
	 * @param args array of commandline arguments
	 */
	public Runner(String[] args)
	{
		parseArgs(args);
	}
	
	/**
	 * Parses the arguments
	 * @param args
	 */
	private void parseArgs(String[] args)
	{
		CommandLineParser parser = new DefaultParser();
		
		try
		{
			line = parser.parse(getOptions(), args);
		}
		catch (ParseException e)
		{		
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Returns the cmd options
	 * @return
	 */
	private Options getOptions()
	{
		Options opts = new Options();
		
		//Cassandra Cluster		
		opts.addOption
		(
			Option
			.builder("c")
			.longOpt("cassandra")
			.hasArg(true)
			.desc("IPs of the Cassandra cluster")			
			.build()			
		);
		
		//Cassandra keyspace
		opts.addOption
		(
			Option
			.builder("k")
			.longOpt("keyspace")
			.hasArg(true)
			.desc("Keyspace of the Cassandra cluster")
			.build()
		);
		
		// Elastic Cluster		
		opts.addOption
		(
			Option
			.builder("e")
			.longOpt("elastic")
			.hasArg(true)
			.desc("IP of the Elastic cluster")
			.build()
		);
		
		// Table
		opts.addOption
		(
			Option
			.builder("t")
			.longOpt("table")
			.hasArg(true)
			.desc("Table to export to Elastic")
			.build()
		);
		
		//Help
		opts.addOption
		(
			Option
			.builder("h")
			.longOpt("help")
			.hasArg(false)
			.desc("Print this help text")
			.build()
		);
		
		//Debug
		opts.addOption
		(
			Option
			.builder("d")
			.longOpt("debug")
			.hasArg(false)
			.desc("Show Debug messages")
			.build()
		);
		
		return opts;
	}
	
	/**
	 * Checks if all required arguments are present
	 * @return
	 */
	private boolean checkArgs()
	{
		boolean result = false;
		
		if (line != null)
		{
			result = line.hasOption('c') && line.hasOption('e') && line.hasOption('t') && line.hasOption('k');
		}
		
		debug = line.hasOption('d');
		
		if (debug)
		{
			System.out.println("CMD Parameter checked result: " + result);
		}
		return result;
	}
	
	/**
	 * Runs the transfer from Cassandra to Elastic
	 * @throws Exception 
	 */
	public void run() throws Exception
	{
		if (checkArgs())
		{
			if (debug)
			{
				System.out.println("Cassandra IP: " + line.getOptionValue('c'));
				System.out.println("Elastic IP: " + line.getOptionValue('e'));
				System.out.println("Tables: " + line.getOptionValue('t'));
				System.out.println("Keyspace: " + line.getOptionValue('k'));
			}
			
			String keyspace		 = line.getOptionValue('k');			
			String contactPoints = line.getOptionValue('c');
			String elastic		 = line.getOptionValue('e');
			String cmdTables	 = line.getOptionValue('t');			
			
			List<InetSocketAddress> inetAdresses = Transformer.transformToInetAddressList(contactPoints);
			List<String> tables					 = Transformer.transformToStringList(cmdTables);
						
			Database db = new Database(keyspace, inetAdresses);
			if (debug)
			{
				System.out.println("Connection to Cassandra Cluster established");
			}
			Node node = new Node(elastic);
			if (debug)
			{
				System.out.println("Connection to Elastic Cluster established");
			}
			
			for (String table : tables)
			{
				if (debug)
				{
					System.out.println("Starting Export for table: " + table);
				}
				Table t = db.getTableInformation(table);
				
				if (t != null)
				{
					Map<String, Object> map = t.getTableAsMap();
					if (debug)
					{
						System.out.println("Gathering data");
					}
					ResultSet rs = db.select(t.getFields(), t.getName(), "");										
					
					List<Map<String, Object>> data = new ArrayList<>();
					
					for (Row row : rs)
					{
						if (rs.getAvailableWithoutFetching() == 10000 && !rs.isFullyFetched())
						{
							rs.fetchMoreResults();
						}
						
						if (row != null)
						{
							Map<String, Object> clone = new HashMap<String, Object>();
							clone.putAll(map);
							for (Column c : t.getColumns())
							{
								clone.put(c.getName(), row.getObject(c.getName()));
							}
							data.add(clone);
							
						}
					}
					
					if (debug)
					{
						System.out.println("Gathered data");
						System.out.println("Sending data to Elastic");
					}
					node.bulkIndex(keyspace, table, data, 10000);
					
				}
				else
				{
					System.err.println("Table " + table + " not found!");
				}			
			}
			
			db.disconnect();
			if (debug)
			{
				System.out.println("Connection to Cassandra Cluster closed");
			}
			node.close();
			if (debug)
			{
				System.out.println("Connection to Elastic Cluster closed");
			}
			
		}
		else
		{
			HelpFormatter help = new HelpFormatter();
			help.printHelp("CassandraToElastic", getOptions());		
		}
	}
}
