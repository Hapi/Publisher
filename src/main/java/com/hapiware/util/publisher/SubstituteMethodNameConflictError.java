package com.hapiware.util.publisher;


public class SubstituteMethodNameConflictError
	extends
		Error
{
	private static final long serialVersionUID = -3939427103471236096L;

	public SubstituteMethodNameConflictError(String signature, Throwable cause)
	{
		super(
			"\nWrapper interface has a method name which does not exist " 
				+ "in the wrapped class:\n  " + signature + "\n",
			cause
		);
	}
}
