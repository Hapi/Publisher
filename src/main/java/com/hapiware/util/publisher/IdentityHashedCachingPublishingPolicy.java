package com.hapiware.util.publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;


/**
 * An identity hashed caching publishing policy for {@link Publisher}. 
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
final public class IdentityHashedCachingPublishingPolicy<PSI>
	extends
		PublishingPolicyBase
	implements 
		PublishingPolicy<PSI>
{
	private final static Map<Class<?>, Object> _substituteCache = new HashMap<Class<?>, Object>();
	private final Class<?> _substituteInterface;
	
	
	public IdentityHashedCachingPublishingPolicy(Class<?> substituteInterface)
	{
		_substituteInterface = substituteInterface;
	}

	
	@SuppressWarnings("unchecked")
	public PSI publish(final Object obj)
	{
		Object retVal = _substituteCache.get(_substituteInterface);
		if(retVal != null)
			return (PSI)retVal;

		final Map<Object, Method> objMethodCache = new HashMap<Object, Method>();
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
								objMethodCache.put(methodKey, objMethod);
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
		_substituteCache.put(_substituteInterface, retVal);
		return (PSI)retVal;
	}
}
