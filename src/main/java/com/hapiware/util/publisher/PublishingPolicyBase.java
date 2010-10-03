package com.hapiware.util.publisher;

import java.lang.reflect.Method;

/**
 * A base class for other publishing policies.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class PublishingPolicyBase
{
	/**
	 * Creates a signature representation for the given class and method.
	 * 
	 * @param className
	 * 		A class name which method's signature is to be created.
	 * 
	 * @param method
	 * 		A method which signature is to be created.
	 * @return
	 * 		A signature
	 */
	protected final static String createSignature(String className, Method method)
	{
		String signature = className + "." + method.getName() + "(";
		Class<?> types[] = method.getParameterTypes();
		if(types.length > 0) {
			for(int i = 0; i < types.length - 1; i++)
				signature += types[i].getName() + ",";
			signature += types[types.length - 1].getName();
		}
		signature += ")";
		return signature;
	}
}
