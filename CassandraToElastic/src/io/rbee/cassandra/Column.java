package io.rbee.cassandra;

/**
 * Represents a Column of a Table
 * @author Sven Ulrich
 *
 */
public class Column
{
	/** Fields **/
	private String name;
	private String type;
	
	/** Constructor **/
	
	/**
	 * Creates a new instance of this class
	 */
	public Column()
	{
		/** EMPTY **/
	}
	
	/**
	 * Creates a new instance of this class
	 * @param name
	 * @param type
	 */
	public Column(String name, String type)
	{
		setName(name);
		setType(type);
	}

	/** Setter **/
	
	public void setName(String name)
	{
		this.name = name;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	/** Getter **/
	
	public String getName()
	{
		return name;
	}

	public String getType()
	{
		return type;
	}	
}
