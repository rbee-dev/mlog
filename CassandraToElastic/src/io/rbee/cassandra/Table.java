package io.rbee.cassandra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Table
 * @author Sven Ulrich
 *
 */
public class Table
{
	/** Fields */
	private String		 name;
	private List<Column> columns;
	private String		 keyspace;
	
	/** Constructor **/
	
	/**
	 * Creates a new instance of this class
	 */
	public Table()
	{
		this.columns = new ArrayList<>();
	}
	
	/**
	 * Creates a new instance of this class
	 * @param name
	 * @param keyspace
	 */
	public Table(String name, String keyspace)
	{
		this();
		setName(name);
		setKeyspace(keyspace);
	}

	/** Setter **/
	
	public void setName(String name)
	{
		this.name = name;
	}

	public void setColumns(List<Column> columns)
	{
		this.columns = columns;
	}

	public void setKeyspace(String keyspace)
	{
		this.keyspace = keyspace;
	}

	/** Getter **/
	
	public String getName()
	{
		return name;
	}

	public List<Column> getColumns()
	{
		return columns;
	}

	public String getKeyspace()
	{
		return keyspace;
	}
	
	/** public **/
	
	/**
	 * Adds a Column to the Table
	 * @param name
	 * @param type
	 */
	public void addColumn(String name, String type)
	{
		Column column = new Column(name, type);
		addColumn(column);
	}
	
	/**
	 * Adds a Column to the Table
	 * @param column
	 */
	public void addColumn(Column column)
	{
		this.columns.add(column);
	}
	
	/**
	 * Returns all columns as Map. The names of the columns are used as keys.
	 * @return
	 */
	public Map<String, Object> getTableAsMap()
	{
		Map<String, Object> map = new HashMap<>();
		
		for (Column c : columns)
		{
			map.put(c.getName(), null);
		}
		
		return map;
	}
	
	/**
	 * Returns all columns as List
	 * @return
	 */
	public List<String> getFields()
	{
		List<String> fields = new ArrayList<>();
		for (Column c : columns)
		{
			fields.add(c.getName());
		}
		
		return fields;
	}
	
}
