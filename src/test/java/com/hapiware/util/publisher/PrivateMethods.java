package com.hapiware.util.publisher;

public class PrivateMethods
{
	@SuppressWarnings("unused")
	private static void doSomething()
	{
		// Does nothing. This just for testing void return value in the substitute.
	}
	
	@SuppressWarnings("unused")
	private final String superAlgorithm(String value, int num)
	{
		String retVal = "";
		for(int i = 0; i < num; i++)
			retVal += value;
		return retVal;
	}
	
	@SuppressWarnings("unused")
	private String add(String a, String b)
	{
		return a + b;
	}
	
	@SuppressWarnings("unused")
	private double add(double a, double b)
	{
		return a + b;
	}
	
	@SuppressWarnings("unused")
	private int add(int a, int b)
	{
		return a + b;
	}
	
	@SuppressWarnings("unused")
	private static byte[] concat(byte[] left, byte[] right)
	{
		byte[] retVal = new byte[left.length + right.length];
		System.arraycopy(left, 0, retVal, 0, left.length);
		System.arraycopy(right, 0, retVal, left.length, right.length);
		return retVal;
	}
	
}
