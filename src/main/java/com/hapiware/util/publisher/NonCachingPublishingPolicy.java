package com.hapiware.util.publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * A non-caching publishing policy for {@link Publisher}.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @param <PSI>
 * 		A public substitute interface.
*/
final public class NonCachingPublishingPolicy<PSI>
	extends
		PublishingPolicyBase
	implements 
		PublishingPolicy<PSI>
{
	private final Class<?> _substituteInterface;
	
	public NonCachingPublishingPolicy(Class<?> substituteInterface)
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
		return
			(PSI)Proxy.newProxyInstance(
				_substituteInterface.getClassLoader(),
				new Class[] {_substituteInterface},
				new InvocationHandler()
				{
					public Object invoke(Object proxy, Method siMethod, Object[] args)
						throws
							Throwable
					{
						try {
							Method objMethod =
								substitutedClass.getDeclaredMethod(
									siMethod.getName(),
									siMethod.getParameterTypes()
								);
							objMethod.setAccessible(true);
							return objMethod.invoke(substitutedObject, args);
						}
						catch(NoSuchMethodException ex) {
							throw 
								new SubstituteMethodNameConflictError(
									createSignature(_substituteInterface.toString(), siMethod),
									ex
								);
						}
						catch(NullPointerException ex) {
							throw 
								new StaticMethodConflictError(
									createSignature(_substituteInterface.toString(), siMethod),
									ex
								);
						}
					}
				}
			);
	}
}
