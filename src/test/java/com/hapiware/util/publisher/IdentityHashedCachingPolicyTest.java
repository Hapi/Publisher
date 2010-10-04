package com.hapiware.util.publisher;

import static junit.framework.Assert.assertEquals;

import org.testng.annotations.Test;

import com.hapiware.util.publisher.annotation.IdentityHashCaching;


public class IdentityHashedCachingPolicyTest
{
	private interface SPublisher
	{
		public <PSI> PublishingPolicy<PSI> findPublishingPolicy(
			final Class<PSI> substituteInterface
		);
	}
	
	@IdentityHashCaching
	private interface SPrivateMethods
	{
		public void doSomething();
		public String superAlgorithm(String value, int num);
		public String add(String a, String b);
		public double add(double a, double b);
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
			"com.hapiware.util.publisher.IdentityHashedCachingPublishingPolicy",
			pp.getClass().getName()
		);
	}
}
