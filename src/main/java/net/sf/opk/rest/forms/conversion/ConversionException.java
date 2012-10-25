package net.sf.opk.rest.forms.conversion;

/**
 * Exception that occurs in {@link ConversionService} or in {@link Converter} implementations.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class ConversionException extends RuntimeException
{
	/**
	 * Create a {@code ConversionException} with the specified message.
	 *
	 * @param message a message describing what went wrong
	 */
	public ConversionException(String message)
	{
		super(message);
	}
}
