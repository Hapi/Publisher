package com.hapiware.util.publisher;


/**
 * {@code PublishingPolicy} creates a public substitute object for {@link Publisher}.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @param <PSI>
 * 		A public substitute interface.
 */
public interface PublishingPolicy<PSI>
{
	/**
	 * Returns a substitute object with a proper {@link InvocationHandler} attached to it.
	 * 
	 * @param substitutedObject
	 * 		An object to be substituted (i.e. the object which does the real work).
	 * 
	 * @return
	 * 		A substitute object.
	 * 
	 * @throws SubstituteMethodNameConflictError
	 * 		If substitute interface has a method which does not exist in the substituted class. 
	 */
	public PSI publish(final Object substitutedObject);
	
	/**
	 * Returns a substitute object with a proper {@link InvocationHandler} attached to it.
	 * This method is designed to be used for static methods in {@code substitutedClass}.
	 * 
	 * @param substitutedClass
	 * 		A class to be substituted (i.e. the class which static methods do the real work).
	 * 
	 * @return
	 * 		A substitute object.
	 * 
	 * @throws SubstituteMethodNameConflictError
	 * 		If substitute interface has a method which does not exist in the substituted class.
	 * 
	 * @throws StaticMethodConflictError 
	 * 		If substitute interface has a method which is an instance method instead of being
	 * 		a static method in the substituted class.
	 */
	public PSI publish(final Class<?> substitutedClass);
}
