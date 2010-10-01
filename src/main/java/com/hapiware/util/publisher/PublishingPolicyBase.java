package com.hapiware.util.publisher;

import java.lang.reflect.Method;

public class PublishingPolicyBase
{
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
