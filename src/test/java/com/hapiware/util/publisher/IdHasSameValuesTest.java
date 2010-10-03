package com.hapiware.util.publisher;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import org.testng.annotations.Test;

import com.hapiware.util.publisher.annotation.Id;
import com.hapiware.util.publisher.annotation.IdAnnotationError;

public class IdHasSameValuesTest
{
	private interface SPrivateMethods
	{
		@Id(0)
		public void doSomething();
		@Id(1)
		public String superAlgorithm(String value, int num);
		@Id(1)
		public String add(String a, String b);
		@Id(2)
		public double add(double a, double b);
		@Id(3)
		public int add(int a, int b);
	}
	@Test
	public void idsMustBeSequential()
	{
		try {
			PrivateMethods pm = new PrivateMethods();
			SPrivateMethods substitute =
				Publisher.publish(SPrivateMethods.class, pm);
			substitute.doSomething();
			fail();
		}
		catch(IdAnnotationError e) {
			assertEquals("Same @Id values are not allowed.", e.getMessage());
		}
	}
}
