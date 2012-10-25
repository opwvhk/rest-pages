package net.sf.opk.rest.forms.conversion;

import java.util.List;

import com.fasterxml.classmate.ResolvedType;


/**
 * Base class for the default converters.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public abstract class SingleValueConverter implements Converter
{
	protected boolean isSingleStringParameter(Class<?>[] parameterTypes)
	{
		return parameterTypes.length == 1 && String.class.isAssignableFrom(parameterTypes[0]);
	}


	@Override
	public <T> T convertTo(ResolvedType resolvedType, List<String> values)
	{
		String value = getFirstElement(values);
		if (value == null || value.isEmpty())
		{
			return null;
		}
		else
		{
			return convertTo(resolvedType, value);
		}
	}


	/**
	 * Get the firt element from a list.
	 *
	 * @param values a list
	 * @return the first element, or {@code null} if the list is empty
	 */
	private <T> T getFirstElement(List<T> values)
	{
		T value = null;
		if (values.size() >= 1)
		{
			value = values.get(0);
		}
		return value;
	}


	public abstract <T> T convertTo(ResolvedType resolvedType, String value);
}
