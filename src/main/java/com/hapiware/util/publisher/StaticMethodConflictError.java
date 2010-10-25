package com.hapiware.util.publisher;


/**
 * Thrown by {@link PublishingPolicy} to show that a substitute interface has an instance
 * method instead of a static method in the substituted class. See {@link Publisher} documentation
 * for more information.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @see Publisher
 */
public class StaticMethodConflictError
	extends
		Error
{
	private static final long serialVersionUID = -2315512656830317447L;

	/**
	 * Constructs a {@code StaticMethodConflictError} with a cause.
	 * 
	 * @param signature
	 * 		A signature of the substitute interface.
	 * 
	 * @param cause
	 * 		A cause.
	 */
	public StaticMethodConflictError(String signature, Throwable cause)
	{
		super(
			"\nSubstitute interface has a method name which is an instance method " 
				+ "in the substituted class:\n  " + signature + "\n",
			cause
		);
	}
}
