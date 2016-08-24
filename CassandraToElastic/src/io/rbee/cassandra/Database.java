package io.rbee.cassandra;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;

public class Database
{
	/** Fields **/
	
	private Cluster					 cluster;
	private List<InetSocketAddress>	 contactPoints;	
	private Session					 session;
	private String					 keyspace;
	private boolean					 connected;
	
	/** Constructor **/
	
	/**
	 * Creates a new instance of this class
	 */
	public Database()
	{
		init();
		connect();
	}
	
	/**
	 * Creates a new instance of this class
	 * @param keyspace
	 * @param contactPoints
	 */
	public Database(String keyspace, List<InetSocketAddress> contactPoints)
	{
		init();
		this.keyspace = keyspace;
		this.contactPoints = contactPoints;
		connect();
	}
	
	/**
	 * initializes the default settings
	 */
	private void init()
	{
		this.contactPoints = new ArrayList<>();
		
		InetSocketAddress localhost = new InetSocketAddress("127.0.0.1", 9042);
		this.contactPoints.add(localhost);
		
		this.keyspace		 = "rbee";		
	}
	
	/**
	 * Opens a connection to the Cassandra cluster
	 */
	private void connect()
	{
		try
		{
			PoolingOptions poolingOpts = new PoolingOptions();
			poolingOpts.setCoreConnectionsPerHost(HostDistance.REMOTE, 2);
			poolingOpts.setMaxConnectionsPerHost(HostDistance.REMOTE, 200);
			
			disconnect();
			
			this.cluster = Cluster.builder()
							.addContactPointsWithPorts(this.contactPoints)
							.withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
							.withPoolingOptions(poolingOpts)
							.build();
			this.session = this.cluster.connect(this.keyspace);
			
			connected = !this.session.isClosed();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
	
	/**
	 * Closes the session
	 */
	private void closeSession()
	{
		if (this.session != null)
		{
			if (!this.session.isClosed())
			{
				this.session.close();
			}
		}
	}
	
	/**
	 * Closes the database connection
	 */
	private void close()
	{
		if (this.cluster != null)
		{
			if (!this.cluster.isClosed())
			{
				this.cluster.close();
			}
		}
	}
	
	/**
	 * Executes the given statement. Returns a ResultSet if the call was successfull
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	private ResultSet execute(BoundStatement statement) throws Exception
	{
		return execute(statement, this.session);		
	}
	
	/**
	 * Executes the given statement. Returns a ResultSet if the call was successfull
	 * @param statement
	 * @param session
	 * @return
	 * @throws Exception
	 */
	private ResultSet execute(BoundStatement statement, Session session)
	{
		ResultSet rs = null;
		try
		{
			rs = session.execute(statement);
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
		
		return rs;
	}

	/**
	 * Returns a BoundStatement from the given String. Uses the default Session to the keyspace.
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	private BoundStatement getBoundStatement(String statement) throws Exception
	{
		return getBoundStatement(statement, this.session);		
	}
	
	/**
	 * Returns a BoundStatement from the given String. Uses the given Session.
	 * @param statement
	 * @param session
	 * @return
	 * @throws Exception
	 */
	private BoundStatement getBoundStatement(String statement, Session session)
	{
		PreparedStatement ps = session.prepare(statement);
		BoundStatement bs	 = new BoundStatement(ps);
		return bs;
	}
	
	/**
	 * Sets the param in the BoundStatement
	 * @param bs
	 * @param value
	 * @param index
	 */
	private void setParam(BoundStatement bs, Object value, int index)
	{
		if (value instanceof String)
		{
			bs.setString(index, (String) value);
		}
		else if (value instanceof Integer)
		{
			bs.setInt(index, (Integer) value);
		}
		else if (value instanceof Long)
		{
			bs.setLong(index, (Long) value);
		}
		else if (value instanceof Float)
		{
			bs.setFloat(index, (Float) value);
		}
		else if (value instanceof Double)
		{
			bs.setDouble(index, (Double) value);
		}
		else if (value instanceof Short)
		{
			bs.setShort(index, (Short) value);
		}
		else if (value instanceof Boolean)
		{
			bs.setBool(index, (Boolean) value);
		}
	}


	/** Setter **/
	
	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}	
	
	/** Getter **/
	
	public boolean isConnected()
	{
		return connected;
	}


	/**
	 * Closes all open connections to the database
	 */
	public void disconnect()
	{
		closeSession();
		close();
	}
	
	
	public Table getTableInformation(String tablename) throws Exception
	{
		if (!this.connected)
		{
			throw new Exception("Connection not established; please establish a Connection first");
		}
		
		Table table = null;
		
		Session system = this.cluster.connect("system_schema");
		
		String statement	 = "select table_name from tables where keyspace_name ='" + this.keyspace + "'";
		BoundStatement bs	 = getBoundStatement(statement, system);
		ResultSet rs		 = execute(bs, system);
		boolean found		 = false;
		
		if (rs != null)
		{
			for (Row row : rs)
			{
				if (row.getString("table_name").equals(tablename))
				{
					found = true;
					break;
				}
			}
		}
		
		if (found)
		{
			table = new Table(tablename, this.keyspace);
			String columnStatement = "select column_name, type from columns where keyspace_name='" + this.keyspace + "' and table_name='" + tablename + "'";
			bs = getBoundStatement(columnStatement, system);
			ResultSet rSet = execute(bs, system);
			
			if (rs != null)
			{
				for (Row row : rSet)
				{
					table.addColumn(row.getString("column_name"), row.getString("type"));					
				}
			}
			
		}
		
		return table;
	}
	
	/**
	 * Inserts the given statement into the database
	 * @param statement
	 * @param values
	 * @throws Exception
	 */
	public void insert(String statement, Collection<Object> values) throws Exception
	{
		BoundStatement bs = getBoundStatement(statement);
		
		int i = 0;
		for(Object value : values)
		{
			setParam(bs, value, i);
			i++;
		}
		
		if(execute(bs) == null)
		{			
			throw new Exception("Error inserting data");
		}
	}
	
	public ResultSet select(String fields, String table, String whereClause) throws Exception
	{
		return select(fields, ";", table, whereClause);
	}
	
	public ResultSet select(String fields, String delimiter, String table, String whereClause) throws Exception
	{
		String[] array 		= fields.split(delimiter);
		List<String> list 	= Arrays.asList(array);
		
		return select(list, table, whereClause);
	}
	
	public ResultSet select(List<String> fields, String table, String whereClause) throws Exception
	{
		String statement = "select ";
		
		if (fields != null)
		{
			for (String s : fields)
			{
				statement += s + ",";				
			}
			
			statement = statement.substring(0, statement.length() -1);		
		}
		else
		{
			statement += "*";
		}
		
		statement += " from " + table;
		
		if ((whereClause != null) && !whereClause.isEmpty())
		{
			statement += " where " + whereClause;
		}
		
		BoundStatement bs = getBoundStatement(statement);
		
		return execute(bs);
	}
}
