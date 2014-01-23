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
