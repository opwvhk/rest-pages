package net.sf.opk.rest.forms.conversion;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.rest.util.Prioritized;

import static java.lang.String.format;


/**
 * Converter for arrays.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class ArrayConverter implements Converter, Prioritized
{
	/**
	 * The conversion service used to convert array elements.
	 */
	private ConversionService conversionService;


	/**
	 * Create an array converter.
	 *
	 * @param conversionService the conversion service used for array elements
	 */
	@Inject
	public ArrayConverter(ConversionService conversionService)
	{
		this.conversionService = conversionService;
	}


	@Override
	public boolean canConvertTo(ResolvedType resolvedType)
	{
		return resolvedType.isArray();
	}


	@Override
	public <T> T convertTo(ResolvedType resolvedType, List<String> values)
	{
		if (!canConvertTo(resolvedType))
		{
			throw new ConversionException(format("%s is not an array type", resolvedType));
		}
		ResolvedType arrayElementType = resolvedType.getArrayElementType();

		Object array = Array.newInstance(arrayElementType.getErasedType(), values.size());
		int index = 0;
		for (String value : values)
		{
			Object convertedValue = conversionService.convert(Collections.singletonList(value), arrayElementType);
			Array.set(array, index++, convertedValue);
		}

		return (T)array;
	}


	@Override
	public int getPriority()
	{
		return Integer.MIN_VALUE + 2;
	}
}
