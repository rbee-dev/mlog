package io.rbee.cassandra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table
{
	private String		 name;
	private List<Column> columns;
	private String		 keyspace;
	
	public Table()
	{
		this.columns = new ArrayList<>();
	}
	
	public Table(String name, String keyspace)
	{
		this();
		setName(name);
		setKeyspace(keyspace);
	}

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
	
	public void addColumn(String name, String type)
	{
		Column column = new Column(name, type);
		addColumn(column);
	}
	
	public void addColumn(Column column)
	{
		this.columns.add(column);
	}
	
	public Map<String, Object> getTableAsMap()
	{
		Map<String, Object> map = new HashMap<>();
		
		map.put("name", getName());
		map.put("keyspace", getKeyspace());
		
		for (Column c : columns)
		{
			map.put(c.getName(), null);
		}
		
		return map;
	}
	
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
