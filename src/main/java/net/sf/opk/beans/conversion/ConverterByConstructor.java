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

import java.lang.reflect.Constructor;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.beans.util.Prioritized;
import net.sf.opk.beans.util.BeanUtil;
import net.sf.opk.beans.util.Cache;

import static java.lang.String.format;


/**
 * Converter that can convert to any class with a constructor that takes a single String.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class ConverterByConstructor extends SingleValueConverter implements Prioritized
{
	private Cache<Class<?>, Constructor<?>> constructorCache = new Cache<>();


	@Override
	public boolean canConvertTo(ResolvedType resolvedType)
	{
		return getConstructor(resolvedType.getErasedType()) != null;
	}


	private <T> Constructor<T> getConstructor(Class<T> clazz)
	{
		Constructor<T> constructor = (Constructor<T>)constructorCache.get(clazz);
		if (constructor == null)
		{
			constructor = findSingleStringConstructor(clazz);
			constructorCache.put(clazz, constructor);
		}
		return constructor;
	}


	private <T> Constructor<T> findSingleStringConstructor(Class<T> clazz)
	{
		for (Constructor<T> constructor : (Constructor<T>[])clazz.getConstructors())
		{
			if (isSingleStringParameter(constructor.getParameterTypes()))
			{
				return constructor;
			}
		}
		return null;
	}


	@Override
	public <T> T convertTo(ResolvedType resolvedTypez, String value)
	{
		Class<T> clazz = (Class<T>)resolvedTypez.getErasedType();
		Constructor<T> constructor = getConstructor(clazz);
		if (constructor == null)
		{
			throw new ConversionException(format(
					"Cannot convert to %s: the class has no constructor that takes a single String.", clazz));
		}
		return BeanUtil.instantiate(constructor, value);
	}


	@Override
	public int getPriority()
	{
		return Integer.MIN_VALUE + 1;
	}
}
