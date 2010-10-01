package com.hapiware.util.publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.hapiware.util.publisher.annotation.Id;

public class CachingPublisher<INTERFACE>
	extends
		PublishingPolicyBase
	implements 
		PublishingPolicy<INTERFACE>
{
	private final static Map<Class<?>, Object> _publicationCache = new HashMap<Class<?>, Object>();
	private final Class<?> _publicationInterface;
	
	public CachingPublisher(Class<?> publicationInterface)
	{
		_publicationInterface = publicationInterface;
	}

	@SuppressWarnings("unchecked")
	public INTERFACE publish(final Object obj)
	{
		Object retVal = _publicationCache.get(_publicationInterface);
		if(retVal != null)
			return (INTERFACE)retVal;

		final Method[] methodCache = new Method[_publicationInterface.getDeclaredMethods().length];
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
						Id id = method.getAnnotation(Id.class);
						Method m = methodCache[id.value()];
						try {
							if(m == null) {
								m =
									obj.getClass().getDeclaredMethod(
										method.getName(),
										method.getParameterTypes()
									);
								m.setAccessible(true);
								methodCache[id.value()] = m;
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
		_publicationCache.put(_publicationInterface, retVal);
		return (INTERFACE)retVal;
	}

}
