package com.hapiware.util.publisher;
import static junit.framework.Assert.assertEquals;

import org.testng.annotations.Test;


import com.hapiware.util.publisher.Publisher;
import com.hapiware.util.publisher.annotation.Id;


public class IdOkTest
{
	private interface SPrivateMethods
	{
		@Id(0)
		public void doSomething();
		@Id(1)
		public String superAlgorithm(String value, int num);
		@Id(2)
		public String add(String a, String b);
		@Id(3)
		public double add(double a, double b);
		@Id(4)
		public int add(int a, int b);
	}
	@Test
	public void idOk()
	{
		PrivateMethods pm = new PrivateMethods();
		SPrivateMethods substitute = Publisher.publish(SPrivateMethods.class, pm);
		substitute.doSomething();
		String str = "miu";
		str = substitute.superAlgorithm(str, 3);
		assertEquals("miumiumiu", str);
		assertEquals(616, substitute.add(313, 303));
		assertEquals(-270.02, substitute.add(3.14, -273.16), 0.000001);
		assertEquals("Hello World!", substitute.add("Hello ", "World!"));
	}
}
