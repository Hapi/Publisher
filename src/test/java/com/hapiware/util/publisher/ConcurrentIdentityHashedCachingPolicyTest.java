package com.hapiware.util.publisher;

import static junit.framework.Assert.assertEquals;

import org.testng.annotations.Test;

import com.hapiware.util.publisher.annotation.ConcurrentIdentityHashCaching;


public class ConcurrentIdentityHashedCachingPolicyTest
{
	private interface SPublisher
	{
		public <INTERFACE> PublishingPolicy<INTERFACE> findPublishingPolicy(
			final Class<INTERFACE> substituteInterface
		);
	}
	
	@ConcurrentIdentityHashCaching
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
			"com.hapiware.util.publisher.ConcurrentIdentityHashedCachingPublishingPolicy",
			pp.getClass().getName()
		);
	}
}
