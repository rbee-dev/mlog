package io.rbee;

import io.rbee.system.Runner;

public class Main
{
	public static void main(String[] args) throws Exception 
	{		
		long start = System.currentTimeMillis();
		Runner runner = new Runner(args);
		runner.run();				
		System.out.println("Count: " + runner.getSendCount());
		System.out.println("Runtime: " + (System.currentTimeMillis() - start));

	}
	

}
