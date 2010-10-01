package com.hapiware.util.publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class NonCachingPublisher<INTERFACE>
	extends
		PublishingPolicyBase
	implements 
		PublishingPolicy<INTERFACE>
{
	private final Class<?> _publicationInterface;
	
	public NonCachingPublisher(Class<?> publicationInterface)
	{
		_publicationInterface = publicationInterface;
	}

	@SuppressWarnings("unchecked")
	public INTERFACE publish(final Object obj)
	{
		return
			(INTERFACE)Proxy.newProxyInstance(
				_publicationInterface.getClassLoader(),
				new Class[] {_publicationInterface},
				new InvocationHandler()
				{
					public Object invoke(Object proxy, Method method, Object[] args)
						throws
							Throwable
					{
						try {
							Method m =
								obj.getClass().getDeclaredMethod(
									method.getName(),
									method.getParameterTypes()
								);
							m.setAccessible(true);
							return m.invoke(obj, args);
						}
						catch(NoSuchMethodException ex) {
							throw 
								new WrapperMethodNameConflictError(
									createSignature(_publicationInterface.toString(), method),
									ex
								);
						}
					}
				}
			);
	}

}
