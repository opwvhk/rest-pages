package net.sf.opk.beans;

/**
 * Exception that occurs in {@link BeanProperty} implementations.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class BeanPropertyException extends RuntimeException
{
	/**
	 * Create a {@code BeanPropertyException} with the specified message.
	 *
	 * @param message    a message describing what went wrong
	 * @param parameters parameters for the message (used with {@link String#format(String, Object...)})
	 */
	public BeanPropertyException(String message, Object... parameters)
	{
		super(String.format(message, parameters));
	}


	/**
	 * Create a {@code BeanPropertyException} with the specified message and underlying cause.
	 *
	 * @param cause      the cause for this exception
	 * @param message    a message describing what went wrong
	 * @param parameters parameters for the message (used with {@link String#format(String, Object...)})
	 */
	public BeanPropertyException(Throwable cause, String message, Object... parameters)
	{
		super(String.format(message, parameters), cause);
	}
}
