package com.hapiware.util.publisher;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import org.testng.annotations.Test;

import com.hapiware.util.publisher.annotation.Id;
import com.hapiware.util.publisher.annotation.IdAnnotationError;

public class IdIsMissingTest
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
		// The last id is missing.
		public int add(int a, int b);
	}
	@Test
	public void idIsMissing()
	{
		try {
			PrivateMethods pm = new PrivateMethods();
			SPrivateMethods substitute =
				Publisher.publish(SPrivateMethods.class, pm);
			substitute.doSomething();
			fail();
		}
		catch(IdAnnotationError e) {
			assertEquals("Every substitute method needs @Id.", e.getMessage());
		}
	}
}
