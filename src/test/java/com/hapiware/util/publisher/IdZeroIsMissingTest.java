package com.hapiware.util.publisher;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import org.testng.annotations.Test;

import com.hapiware.util.publisher.annotation.Id;
import com.hapiware.util.publisher.annotation.IdAnnotationError;

public class IdZeroIsMissingTest
{
	private interface SPrivateMethods
	{
		@Id(1)
		public void doSomething();
		@Id(2)
		public String superAlgorithm(String value, int num);
		@Id(3)
		public String add(String a, String b);
		@Id(4)
		public double add(double a, double b);
		@Id(5)
		public int add(int a, int b);
	}
	@Test
	public void idMustStartFromZero()
	{
		try {
			PrivateMethods pm = new PrivateMethods();
			SPrivateMethods substitute =
				Publisher.publish(SPrivateMethods.class, pm);
			substitute.doSomething();
			fail();
		}
		catch(IdAnnotationError e) {
			assertEquals("@Id values must start from zero (0).", e.getMessage());
		}
	}

}
