package com.hapiware.util.publisher;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.hapiware.util.publisher.annotation.ConcurrentIdentityHashed;
import com.hapiware.util.publisher.annotation.Id;
import com.hapiware.util.publisher.annotation.IdentityHashed;


public class Publisher<INTERFACE>
{
	private PublishingPolicy<INTERFACE> _publishingPolicy = null; 
	
	
	private Publisher()
	{
		// Prevents a construction of Publisher.
	}
	
	private Publisher(PublishingPolicy<INTERFACE> publishingPolicy)
	{
		_publishingPolicy = publishingPolicy;
	}
	
	public INTERFACE publish(final Object obj)
	{
		return (INTERFACE)_publishingPolicy.publish(obj);
	}
	
	public static <INTERFACE> Publisher<INTERFACE> create(
		final Class<INTERFACE> publicationInterface
	)
	{
		Publisher<INTERFACE> p = new Publisher<INTERFACE>();
		p._publishingPolicy = findPublishingPolicy(publicationInterface);
		return p;
	}

	private static <INTERFACE> PublishingPolicy<INTERFACE> findPublishingPolicy(
		final Class<INTERFACE> publicationInterface
	)
	{
		if(publicationInterface.isAnnotationPresent(IdentityHashed.class))
			return new IdentityHashedCachingPublisher<INTERFACE>(publicationInterface);
		if(publicationInterface.isAnnotationPresent(ConcurrentIdentityHashed.class))
			return new ConcurrentIdentityHashedCachingPublisher<INTERFACE>(publicationInterface);

		Method[] methods = publicationInterface.getDeclaredMethods();
		int numberOfAnnotations = 0;
		int expectedTotalIdSum = 0;
		int i = 0;
		int totalIdSum = 0;
		boolean idZeroFound = false;
		Set<Integer> set = new HashSet<Integer>();
		for(Method m : methods) {
			expectedTotalIdSum += i++;
			Id id = m.getAnnotation(Id.class);
			if(id == null)
				continue;
			
			if(id.value() < 0)
				throw new RuntimeException("@Id values cannot be negative numbers.");
			numberOfAnnotations++;
			if(id.value() == 0)
				idZeroFound = true;
			
			if(!set.add(id.value()))
				throw new RuntimeException("Same @Id values are not allowed");
			totalIdSum += id.value();
		}
		if(numberOfAnnotations == 0)
			return new NonCachingPublisher<INTERFACE>(publicationInterface);
		if(methods.length != numberOfAnnotations)
			throw new RuntimeException("Every method needs @Id (or none of them).");
		if(!idZeroFound)
			throw new RuntimeException("@Id values must start from zero (0).");
		if(expectedTotalIdSum != totalIdSum)
			throw new RuntimeException("@Id values must be sequential.");
		
		return new CachingPublisher<INTERFACE>(publicationInterface);
	}
	
	public static <INTERFACE> INTERFACE publish(
		final Class<INTERFACE> publicationInterface,
		final Object obj
	)
	{
		return (INTERFACE)findPublishingPolicy(publicationInterface).publish(obj);
	}
}
