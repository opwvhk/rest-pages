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
package net.sf.opk.beans;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.rest.forms.conversion.ConversionService;

import static net.sf.opk.rest.util.GenericsUtil.findTypeParameter;


/**
 * A {@code BeanProperty} describing a {@link Map} key.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class MapKey extends NestedBeanProperty
{
	/**
	 * Error message to throw when unsupported beans are given to {@link #getValue(Object)} and {@link #setValue(Object,
	 * Object)}.
	 */
	private static final String WRONG_PROPERTY_TYPE_ERROR = "%s does not implement java.util.Map";
	/**
	 * Error message to throw when unsupported values are given to {@link #setValue(Object, Object)}.
	 */
	private static final String WRONG_VALUE_TYPE_ERROR = "Cannot set mapped property: %s is not a %s.";
	/**
	 * Conversion service; used for map keys.
	 */
	private ConversionService conversionService;
	/**
	 * Parent property. This property is a nested property, so the parent property handles all but the last segment.
	 */
	private BeanProperty parent;
	/**
	 * The key to represent.
	 */
	private String key;


	/**
	 * Create a map key.
	 *
	 * @param conversionService the conversion service to use for the map key
	 * @param parent            the propety handling everything but the last path segment
	 * @param key               the key to represent
	 */
	public MapKey(ConversionService conversionService, BeanProperty parent, String key)
	{
		this.conversionService = conversionService;
		this.parent = parent;
		this.key = key;
	}


	@Override
	public <T> TypedValue<T> getTypedValue(Object javaBean)
	{
		TypedValue<Map<Object, T>> parentTypedValue = parent.getTypedValue(javaBean);
		ResolvedType parentType = parentTypedValue.getType();

		ResolvedType resolvedType = determineValueType(parentType);

		Map<Object, T> parentValue = parentTypedValue.getValue();
		Object keyValue = convertKeyValue(parentTypedValue.getType());

		return new TypedValue<>(resolvedType, parentValue.get(keyValue));
	}


	/**
	 * Determine the element type of the parent bean. Throws an exception if the parent bean is not a List or array.
	 *
	 * @param parentType the type of the parent bean
	 * @return the element type
	 */
	protected ResolvedType determineValueType(ResolvedType parentType)
	{
		if (!parentType.isInstanceOf(Map.class))
		{
			throw new BeanPropertyException(WRONG_PROPERTY_TYPE_ERROR, parentType);
		}
		return findTypeParameter(parentType, Map.class, 1);
	}


	private Object convertKeyValue(ResolvedType parentType)
	{
		if (!parentType.isInstanceOf(Map.class))
		{
			throw new BeanPropertyException(WRONG_PROPERTY_TYPE_ERROR, parentType);
		}
		ResolvedType keyType = findTypeParameter(parentType, Map.class, 0);
		return conversionService.convert(Collections.singletonList(key), keyType);
	}


	@Override
	public void setValue(Object javaBean, Object value)
	{
		TypedValue<Map> parentTypedValue = parent.getTypedValue(javaBean);
		Object keyValue = convertKeyValue(parentTypedValue.getType());

		ResolvedType elementType = determineValueType(parentTypedValue.getType());
		if (value != null && !elementType.getErasedType().isAssignableFrom(value.getClass()))
		{
			throw new BeanPropertyException(WRONG_VALUE_TYPE_ERROR, value.getClass(), elementType);
		}

		Map<Object, Object> parentValue = parentTypedValue.getValue();
		parentValue.put(keyValue, value);
	}
}
