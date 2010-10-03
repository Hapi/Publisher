package com.hapiware.util.publisher;


/**
 * Thrown by {@link PublishingPolicy} to show that a substitute interface has a method which
 * does not exist in the substituted class. See {@link Publisher} documentation for more
 * information.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @see Publisher
 */
public class SubstituteMethodNameConflictError
	extends
		Error
{
	private static final long serialVersionUID = -3939427103471236096L;

	/**
	 * Constructs a {@code SubstituteMethodNameConflictError} with a cause.
	 * 
	 * @param signature
	 * 		A signature of the substitute interface.
	 * 
	 * @param cause
	 * 		A cause.
	 */
	public SubstituteMethodNameConflictError(String signature, Throwable cause)
	{
		super(
			"\nSubstitute interface has a method name which does not exist " 
				+ "in the substituted class:\n  " + signature + "\n",
			cause
		);
	}
}
