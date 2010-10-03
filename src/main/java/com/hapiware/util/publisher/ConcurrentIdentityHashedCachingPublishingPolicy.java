package com.hapiware.util.publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentIdentityHashedCachingPublishingPolicy<INTERFACE>
	extends
		PublishingPolicyBase
	implements 
		PublishingPolicy<INTERFACE>
{
	private final static ConcurrentMap<Class<?>, Object> _substituteCache =
		new ConcurrentHashMap<Class<?>, Object>();
	private final Class<?> _substituteInterface;
	
	
	public ConcurrentIdentityHashedCachingPublishingPolicy(Class<?> substituteInterface)
	{
		_substituteInterface = substituteInterface;
	}

	@SuppressWarnings("unchecked")
	public INTERFACE publish(final Object obj)
	{
		Object retVal = _substituteCache.get(_substituteInterface);
		if(retVal != null)
			return (INTERFACE)retVal;

		final ConcurrentMap<Object, Method> objMethodCache = new ConcurrentHashMap<Object, Method>();
		retVal =
			Proxy.newProxyInstance(
				_substituteInterface.getClassLoader(),
				new Class[] {_substituteInterface},
				new InvocationHandler()
				{
					public Object invoke(Object proxy, Method siMethod, Object[] args)
						throws
							Throwable
					{
						final Integer methodKey = System.identityHashCode(siMethod);
						Method objMethod = objMethodCache.get(methodKey);
						try {
							if(objMethod == null) {
								objMethod =
									obj.getClass().getDeclaredMethod(
										siMethod.getName(),
										siMethod.getParameterTypes()
									);
								objMethod.setAccessible(true);
								objMethodCache.putIfAbsent(methodKey, objMethod);
							}
							return objMethod.invoke(obj, args);
						}
						catch(NoSuchMethodException ex) {
							throw 
								new SubstituteMethodNameConflictError(
									createSignature(_substituteInterface.toString(), siMethod),
									ex
								);
						}
						catch(IllegalArgumentException ex) {
							throw
								new AmbiguousMethodNameError(
									createSignature(_substituteInterface.getName(), siMethod),
									createSignature(_substituteInterface.getName(), objMethod),
									ex
								);
						}
					}
				}
			);
		_substituteCache.putIfAbsent(_substituteInterface, retVal);
		return (INTERFACE)retVal;
	}
}
