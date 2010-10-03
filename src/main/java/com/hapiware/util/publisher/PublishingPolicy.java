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
	 * @param obj
	 * 		An object to be substituted (i.e. the object which does the real work).
	 * 
	 * @return
	 * 		A substitute object.
	 * 
	 * @throws SubstituteMethodNameConflictError
	 * 		If substitute interface has a method which does not exist in the substituted class. 
	 */
	public PSI publish(final Object obj);
}
