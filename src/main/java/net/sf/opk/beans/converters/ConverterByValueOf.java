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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.beans.util.Prioritized;
import net.sf.opk.beans.util.BeanUtil;
import net.sf.opk.beans.util.Cache;

import static java.lang.String.format;


/**
 * Converter that can convert to any class with a static method {@code valueOf(String)}.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class ConverterByValueOf extends SingleValueConverter implements Prioritized
{
	private Cache<Class<?>, Method> valueOfCache = new Cache<>();


	@Override
	public boolean canConvertTo(ResolvedType resolvedType)
	{
		return getValueOfMethod(resolvedType.getErasedType()) != null;
	}


	private Method getValueOfMethod(Class<?> clazz)
	{
		Method valueOfMethod = valueOfCache.get(clazz);
		if (valueOfMethod == null)
		{
			valueOfMethod = findStaticValueOfMethod(clazz);
			valueOfCache.put(clazz, valueOfMethod);
		}
		return valueOfMethod;
	}


	private <T> Method findStaticValueOfMethod(Class<T> clazz)
	{
		for (Method method : clazz.getMethods())
		{
			if (Modifier.isStatic(method.getModifiers()) && "valueOf".equals(method.getName()) &&
			    clazz.isAssignableFrom(method.getReturnType()) && isSingleStringParameter(method.getParameterTypes()))
			{
				return method;
			}
		}
		return null;
	}


	@Override
	public <T> T convertTo(ResolvedType resolvedType, String value)
	{
		Class<T> clazz = (Class<T>)resolvedType.getErasedType();
		Method valueOfMethod = getValueOfMethod(clazz);
		if (valueOfMethod == null)
		{
			throw new ConversionException(format(
					"Cannot convert to %s: the class has no constructor that takes a single String.", clazz));
		}
		return BeanUtil.invoke(null, valueOfMethod, value);
	}


	@Override
	public int getPriority()
	{
		return Integer.MIN_VALUE;
	}
}
