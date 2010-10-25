package com.hapiware.util.publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.hapiware.util.publisher.annotation.Id;


/**
 * A caching publishing policy for {@link Publisher}.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @param <PSI>
 */
final public class CachingPublishingPolicy<PSI>
	extends
		PublishingPolicyBase
	implements 
		PublishingPolicy<PSI>
{
	private final static Map<Class<?>, Object> _substituteCache = new HashMap<Class<?>, Object>();
	private final Class<?> _substituteInterface;
	
	public CachingPublishingPolicy(Class<?> substituteInterface)
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

		final Method[] objMethodCache = new Method[_substituteInterface.getDeclaredMethods().length];
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
						Id id = siMethod.getAnnotation(Id.class);
						Method objMethod = objMethodCache[id.value()];
						try {
							if(objMethod == null) {
								objMethod =
									substitutedClass.getDeclaredMethod(
										siMethod.getName(),
										siMethod.getParameterTypes()
									);
								objMethod.setAccessible(true);
								objMethodCache[id.value()] = objMethod;
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
		_substituteCache.put(_substituteInterface, retVal);
		return (PSI)retVal;
	}
}
