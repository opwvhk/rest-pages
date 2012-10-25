package net.sf.opk.rest.forms.conversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.inject.Inject;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.rest.util.GenericsUtil;
import net.sf.opk.rest.util.Prioritized;

import static java.lang.String.format;


/**
 * Converter for collections.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class CollectionConverter implements Converter, Prioritized
{
	/**
	 * The conversion service used to convert collection elements.
	 */
	private ConversionService conversionService;


	/**
	 * Create an array converter.
	 *
	 * @param conversionService the conversion service used for collection elements
	 */
	@Inject
	public CollectionConverter(ConversionService conversionService)
	{
		this.conversionService = conversionService;
	}


	@Override
	public boolean canConvertTo(ResolvedType resolvedType)
	{
		return resolvedType.isInstanceOf(Collection.class);
	}


	@Override
	public <T> T convertTo(ResolvedType resolvedType, List<String> values)
	{
		if (!canConvertTo(resolvedType))
		{
			throw new ConversionException(format("%s is not a Collection", resolvedType));
		}
		Class<?> collectionClass = resolvedType.getErasedType();
		ResolvedType elementType = GenericsUtil.findTypeParameter(resolvedType, Collection.class, 0);

		Collection<Object> result = createCollection(collectionClass);
		for (String value : values)
		{
			Object convertedValue = conversionService.convert(Collections.singletonList(value), elementType);
			result.add(convertedValue);
		}
		return (T)result;
	}


	private <T> Collection<T> createCollection(Class<?> collectionClass)
	{
		Collection<T> result;
		if (SortedSet.class.isAssignableFrom(collectionClass))
		{
			result = new TreeSet<>();
		}
		else if (Set.class.isAssignableFrom(collectionClass))
		{
			result = new HashSet<>();
		}
		else
		{
			result = new ArrayList<>();
		}
		return result;
	}


	@Override
	public int getPriority()
	{
		return Integer.MIN_VALUE + 3;
	}
}
