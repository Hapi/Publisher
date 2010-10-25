package com.hapiware.util.publisher;


import static junit.framework.Assert.assertEquals;

import org.testng.annotations.Test;


public class PrivateMethodsTest
{
	private interface SPrivateMethods
	{
		public void doSomething();
		public String superAlgorithm(String value, int num);
		public String add(String a, String b);
		public double add(double a, double b);
		public int add(int a, int b);
		public byte[] concat(byte[] left, byte[] right);
	}
	private interface SPrivateMethodsStaticsOnly
	{
		public void doSomething();
		public byte[] concat(byte[] left, byte[] right);
	}
	
	@Test
	public void testAsObject()
	{
		PrivateMethods pm = new PrivateMethods();
		SPrivateMethods spm = Publisher.publish(SPrivateMethods.class, pm);
		assertEquals(3, spm.add(1, 2));
		assertEquals("Hello World", spm.add("Hello", " World"));
		assertEquals(5.32, spm.add(3.14, 2.18), 0.0001);
		assertEquals("HiHiHiHi", spm.superAlgorithm("Hi", 4));
		spm.doSomething();
		
		byte[] left = { 1, 2, 3, 4 };
		byte[] right = { 10, 20, 30, 40, 50 };
		byte[] result = spm.concat(left, right);
		assertEquals(9, result.length);
		int total = 0;
		for(byte b : result)
			total += b;
		assertEquals(160, total);
	}
	
	@Test
	public void testStaticsOnly()
	{
		SPrivateMethodsStaticsOnly spm =
			Publisher.publish(SPrivateMethodsStaticsOnly.class, PrivateMethods.class);
		spm.doSomething();
		
		byte[] left = { 1, 2, 3, 4 };
		byte[] right = { 10, 20, 30, 40, 50 };
		byte[] result = spm.concat(left, right);
		assertEquals(9, result.length);
		int total = 0;
		for(byte b : result)
			total += b;
		assertEquals(160, total);
	}
	
	@Test
	public void testStaticsOnly2()
	{
		SPrivateMethods spm = Publisher.publish(SPrivateMethods.class, PrivateMethods.class);
		spm.doSomething();

		byte[] left = { 1, 2, 3, 4 };
		byte[] right = { 10, 20, 30, 40, 50 };
		byte[] result = spm.concat(left, right);
		assertEquals(9, result.length);
		int total = 0;
		for(byte b : result)
			total += b;
		assertEquals(160, total);
	}
	
	@Test(expectedExceptions = {StaticMethodConflictError.class})
	public void testStaticsOnlyFailure()
	{
		SPrivateMethods spm = Publisher.publish(SPrivateMethods.class, PrivateMethods.class);
		spm.add(1, 3);
	}
	
	
	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void wrongArgument()
	{
		PrivateMethods pm = new PrivateMethods();
		Publisher.publish(pm.getClass(), SPrivateMethods.class);
	}
	
	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void wrongArgument2()
	{
		Publisher.publish(PrivateMethods.class, SPrivateMethods.class);
	}
}
