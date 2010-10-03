package com.hapiware.util.publisher.annotation;


/**
 * {@code IdAnnotationError} is thrown if any of the {@link Id} annotation constraints are broken.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class IdAnnotationError
	extends
		Error
{
	private static final long serialVersionUID = 4934372328899214258L;

	/**
	 * Constructs an {@code IdAnnotationError} with a specified message.
	 * @param message
	 */
	public IdAnnotationError(String message)
	{
		super(message);
	}
}
