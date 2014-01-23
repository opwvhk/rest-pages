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
package net.sf.opk.beans.conversion;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.beans.util.Prioritized;

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
