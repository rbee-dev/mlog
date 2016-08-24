package io.rbee.helper;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Helper class to Transform different data
 * @author Sven Ulrich
 *
 */
public class Transformer
{
	private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;
	
	/**
	 * Transforms a String to a List of InetSocketAddresses
	 * @param inetAddresses
	 * @return
	 */
	public static List<InetSocketAddress> transformToInetAddressList(String inetAddresses)
	{
		List<InetSocketAddress> list = new ArrayList<>();
		
		if (inetAddresses != null && !inetAddresses.isEmpty())
		{
			String[] array = inetAddresses.split(";");
			
			for (String inetAddress : array)
			{
				String[] combined = inetAddress.split(":");
				
				String host = combined[0];
				String port = "9042";
				
				if (combined[1] != null)
				{
					port = combined[1];
				}
				
				InetSocketAddress isa = new InetSocketAddress(host, Integer.parseInt(port));
				list.add(isa);
			}
		}
		
		return list;
	}
	
	/**
	 * Transforms a String to a String list
	 * @param values
	 * @return
	 */
	public static List<String> transformToStringList(String values)
	{
		List<String> list = new ArrayList<>();
		
		if (values != null && !values.isEmpty())
		{
			String[] array = values.split(";");
			list = Arrays.asList(array);
		}
		
		return list;
	}
	
	/**
	 * Transforms a TIMEUUID to Date
	 * @param timeuuid
	 * @return
	 */
	public static Date transformTIMEUUIDtoDate(String timeuuid)
	{
		Date date = null;
		
		UUID uuid	 = UUID.fromString(timeuuid);
		long time	 = (uuid.timestamp()- NUM_100NS_INTERVALS_SINCE_UUID_EPOCH ) / 10000;
		date		 = new Date(time);
		
		return date;
	}
}
