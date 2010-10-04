package com.hapiware.util.publisher;

import static junit.framework.Assert.assertEquals;

import org.testng.annotations.Test;

import com.hapiware.util.publisher.annotation.Id;
import com.hapiware.util.publisher.annotation.NoCaching;


public class NonCachingPolicyWithAnnotationTest
{
	private interface SPublisher
	{
		public <PSI> PublishingPolicy<PSI> findPublishingPolicy(
			final Class<PSI> substituteInterface
		);
	}
	
	@NoCaching
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
	public void policyNameIsOk()
	{
		Publisher<SPublisher> publisher = Publisher.create(SPublisher.class);
		SPublisher substitutePublisher = Publisher.publish(SPublisher.class, publisher);
		PublishingPolicy<SPrivateMethods> pp =
			substitutePublisher.findPublishingPolicy(SPrivateMethods.class);
		assertEquals(
			"com.hapiware.util.publisher.NonCachingPublishingPolicy",
			pp.getClass().getName()
		);
	}
}
