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
package net.sf.opk.rest.forms.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.QueryParam;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.rest.util.Cache;
import net.sf.opk.rest.util.PriorityComparator;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;


/**
 * Conversion service for Strings to objects, like {@link QueryParam} supports.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
@Singleton
public class ConversionService
{
	/**
	 * Cache to store the converter found per resolved type.
	 */
	private static Cache<ResolvedType, Converter> cache = new Cache<>();
	/**
	 * Constructor argument to allow direct CDI access to the available converters in the system. Used to lazily
	 * initialize {@link #converters}, as initializing that field in the constructor causes infinite recursion
	 * when a converter recieves the {@code ConversionService} via (constructor?) injection.
	 */
	private Instance<Converter> availableConverters;
	/**
	 * The available converters, sorted according to their priority.
	 */
	private List<Converter> converters;


	/**
	 * Create a conversion service.
	 *
	 * @param availableConverters all available converters
	 */
	@Inject
	@SuppressWarnings("CdiInjectionPointsInspection")
	public ConversionService(Instance<Converter> availableConverters)
	{
		this.availableConverters = availableConverters;
	}


	/**
	 * Convert a series of values using a resolved type (i.e. with type variables).
	 *
	 * @param values the values to convert
	 * @param type   the resolved type to convert to
	 * @return the converted value
	 */
	public <T> T convert(List<String> values, ResolvedType type)
	{
		Converter converter = cache.get(type);
		if (converter == null)
		{
			for (Converter aConverter : getConverters())
			{
				if (aConverter.canConvertTo(type))
				{
					converter = aConverter;
					cache.put(type, converter);
					break;
				}
			}
		}

		if (converter == null)
		{
			throw new ConversionException(format("Cannot convert to %s: no converter found.", type));
		}
		else
		{
			return converter.convertTo(type, values);
		}
	}


	/**
	 * Get the value of {@link #converters}. Laziliy initializes the field the first time.
	 *
	 * @return the converters to use, sorted by priority
	 */
	private List<Converter> getConverters()
	{
		if (converters == null)
		{
			// Note: Instance implements Iterable, but not Collection.

			List<Converter> sortedConverters = new ArrayList<>();
			for (Converter converter : availableConverters)
			{
				sortedConverters.add(converter);
			}
			Collections.sort(sortedConverters, new PriorityComparator());
			converters = unmodifiableList(sortedConverters);
		}
		return converters;
	}
}
