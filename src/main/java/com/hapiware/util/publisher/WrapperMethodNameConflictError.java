package com.hapiware.util.publisher;


public class WrapperMethodNameConflictError
	extends
		Error
{
	private static final long serialVersionUID = -3939427103471236096L;

	public WrapperMethodNameConflictError(String signature, Throwable cause)
	{
		super(
			"\nWrapper interface has a method name which does not exist " 
				+ "in the wrapped class:\n  " + signature + "\n",
			cause
		);
	}
}
