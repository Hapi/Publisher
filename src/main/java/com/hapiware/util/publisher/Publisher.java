package com.hapiware.util.publisher;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.hapiware.util.publisher.annotation.ConcurrentIdentityHashCaching;
import com.hapiware.util.publisher.annotation.Id;
import com.hapiware.util.publisher.annotation.IdAnnotationError;
import com.hapiware.util.publisher.annotation.IdentityHashCaching;
import com.hapiware.util.publisher.annotation.NoCaching;


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
		final Class<INTERFACE> substituteInterface
	)
	{
		Publisher<INTERFACE> p = new Publisher<INTERFACE>();
		p._publishingPolicy = findPublishingPolicy(substituteInterface);
		return p;
	}

	private static <INTERFACE> PublishingPolicy<INTERFACE> findPublishingPolicy(
		final Class<INTERFACE> substituteInterface
	)
	{
		if(substituteInterface.isAnnotationPresent(NoCaching.class))
			return new NonCachingPublishingPolicy<INTERFACE>(substituteInterface);
		if(substituteInterface.isAnnotationPresent(IdentityHashCaching.class))
			return new IdentityHashedCachingPublishingPolicy<INTERFACE>(substituteInterface);
		if(substituteInterface.isAnnotationPresent(ConcurrentIdentityHashCaching.class))
			return new ConcurrentIdentityHashedCachingPublishingPolicy<INTERFACE>(substituteInterface);

		Method[] methods = substituteInterface.getDeclaredMethods();
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
				throw new IdAnnotationError("@Id values cannot be negative numbers.");
			numberOfAnnotations++;
			if(id.value() == 0)
				idZeroFound = true;
			
			if(!set.add(id.value()))
				throw new IdAnnotationError("Same @Id values are not allowed.");
			totalIdSum += id.value();
		}
		if(numberOfAnnotations == 0)
			return new NonCachingPublishingPolicy<INTERFACE>(substituteInterface);
		if(methods.length != numberOfAnnotations)
			throw new IdAnnotationError("Every substitute method needs @Id.");
		if(!idZeroFound)
			throw new IdAnnotationError("@Id values must start from zero (0).");
		if(expectedTotalIdSum != totalIdSum)
			throw new IdAnnotationError("@Id values must be sequential.");
		
		return new CachingPublishingPolicy<INTERFACE>(substituteInterface);
	}
	
	public static <INTERFACE> INTERFACE publish(
		final Class<INTERFACE> substituteInterface,
		final Object obj
	)
	{
		return (INTERFACE)findPublishingPolicy(substituteInterface).publish(obj);
	}
}
