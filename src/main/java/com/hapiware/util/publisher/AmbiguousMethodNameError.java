package com.hapiware.util.publisher;


public class AmbiguousMethodNameError
	extends
		Error
{
	private static final long serialVersionUID = 8212514328924181867L;

	public AmbiguousMethodNameError(
		String expectedSignature, 
		String wasSignature,
		Throwable cause
	)
	{
		super(
			"\nA cache key conflict. Change the caching policy.\n"
				+ "Wrapped class has multiple methods with a same name and "
				+ "caching policy fetched a wrong method.\n"
				+ "  expected: " + expectedSignature + "\n"
				+ "       was: " + wasSignature + "\n",
			cause
		);
	}
}
