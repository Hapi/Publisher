package com.hapiware.util.publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A concurrent identity hashed caching publishing policy for {@link Publisher}. The map used
 * for {@code ConcurrentIdentityHashedCachingPublishingPolicy} is {@link ConcurrentMap} but
 * otherwise the implementation does not differ from {@link IdentityHashedCachingPublishingPolicy}.
 * <p>
 * The problem with the identity hash caching is that because Java does not have any public identity
 * for objects then the closest relative was selected which is {@link System#identityHashCode(Object)}.
 * Usually this works but there is no guarantee that the {@link System#identityHashCode(Object)}
 * returns a different value for different objects (i.e. substitute interface {@link Method}s for
 * caching purpouses) and thus the caching policy may fail.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @param <PSI>
 * 		A public substitute interface.
 */
final public class ConcurrentIdentityHashedCachingPublishingPolicy<PSI>
	extends
		PublishingPolicyBase
	implements 
		PublishingPolicy<PSI>
{
	private final static ConcurrentMap<Class<?>, Object> _substituteCache =
		new ConcurrentHashMap<Class<?>, Object>();
	private final Class<?> _substituteInterface;
	
	
	public ConcurrentIdentityHashedCachingPublishingPolicy(Class<?> substituteInterface)
	{
		_substituteInterface = substituteInterface;
	}

	
	public PSI publish(final Object substitutedObject)
	{
		return publish(substitutedObject.getClass(), substitutedObject);
	}


	public PSI publish(Class<?> substitutedClass)
	{
		return publish(substitutedClass, null);
	}

	
	@SuppressWarnings("unchecked")
	private PSI publish(final Class<?> substitutedClass, final Object substitutedObject)
	{
		Object retVal = _substituteCache.get(_substituteInterface);
		if(retVal != null)
			return (PSI)retVal;

		final ConcurrentMap<Object, Method> objMethodCache = new ConcurrentHashMap<Object, Method>();
		Object newRetVal =
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
								Method newObjMethod =
									substitutedClass.getDeclaredMethod(
										siMethod.getName(),
										siMethod.getParameterTypes()
									);
								objMethod = objMethodCache.putIfAbsent(methodKey, newObjMethod);
								if(objMethod == null)
									objMethod = newObjMethod;
								objMethod.setAccessible(true);
							}
							return objMethod.invoke(substitutedObject, args);
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
		retVal = _substituteCache.putIfAbsent(_substituteInterface, newRetVal);
		if(retVal == null)
			retVal = newRetVal;
		return (PSI)retVal;
	}
}
