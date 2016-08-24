package io.rbee.cassandra;

public class Column
{
	private String name;
	private String type;
	
	public Column()
	{
		/** EMPTY **/
	}
	
	public Column(String name, String type)
	{
		setName(name);
		setType(type);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public String getType()
	{
		return type;
	}	
}
