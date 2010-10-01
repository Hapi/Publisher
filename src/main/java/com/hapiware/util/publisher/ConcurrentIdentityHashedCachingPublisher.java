package com.hapiware.util.publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentIdentityHashedCachingPublisher<INTERFACE>
	extends
		PublishingPolicyBase
	implements 
		PublishingPolicy<INTERFACE>
{
	private final static ConcurrentMap<Class<?>, Object> _publicationCacheTS =
		new ConcurrentHashMap<Class<?>, Object>();
	private final Class<?> _publicationInterface;
	
	
	public ConcurrentIdentityHashedCachingPublisher(Class<?> publicationInterface)
	{
		_publicationInterface = publicationInterface;
	}

	@SuppressWarnings("unchecked")
	public INTERFACE publish(final Object obj)
	{
		Object retVal = _publicationCacheTS.get(_publicationInterface);
		if(retVal != null)
			return (INTERFACE)retVal;

		final ConcurrentMap<Object, Method> methodCache = new ConcurrentHashMap<Object, Method>();
		retVal =
			Proxy.newProxyInstance(
				_publicationInterface.getClassLoader(),
				new Class[] {_publicationInterface},
				new InvocationHandler()
				{
					public Object invoke(Object proxy, Method method, Object[] args)
						throws
							Throwable
					{
						final Integer methodKey = System.identityHashCode(method);
						Method m = methodCache.get(methodKey);
						try {
							if(m == null) {
								m =
									obj.getClass().getDeclaredMethod(
										method.getName(),
										method.getParameterTypes()
									);
								m.setAccessible(true);
								methodCache.putIfAbsent(methodKey, m);
							}
							return m.invoke(obj, args);
						}
						catch(NoSuchMethodException ex) {
							throw 
								new WrapperMethodNameConflictError(
									createSignature(_publicationInterface.toString(), method),
									ex
								);
						}
						catch(IllegalArgumentException ex) {
							throw
								new AmbiguousMethodNameError(
									createSignature(_publicationInterface.getName(), method),
									createSignature(_publicationInterface.getName(), m),
									ex
								);
						}
					}
				}
			);
		_publicationCacheTS.putIfAbsent(_publicationInterface, retVal);
		return (INTERFACE)retVal;
	}
}
