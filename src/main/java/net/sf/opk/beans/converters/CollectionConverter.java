/*
 * Copyright 2012 Oscar Westra van Holthe - Kind
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package net.sf.opk.beans.converters;

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

import net.sf.opk.beans.ConversionService;
import net.sf.opk.beans.Converter;
import net.sf.opk.beans.util.Prioritized;
import net.sf.opk.beans.util.GenericsUtil;

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
