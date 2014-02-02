/*
 * Copyright 2012-2013 Oscar Westra van Holthe - Kind
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

import net.sf.opk.beans.conversion.ConversionService;

import static net.sf.opk.beans.util.GenericsUtil.findTypeParameter;


/**
 * A {@code BeanProperty} describing a {@link Map} key.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class MapKey extends BeanProperty
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
	private final ConversionService conversionService;
	/**
	 * The key to represent.
	 */
	private final String key;


	/**
	 * Create a map key.
	 *
	 * @param conversionService the conversion service to use for the map key
	 * @param parent            the propety handling everything but the last path segment
	 * @param key               the key to represent
	 */
	public MapKey(ConversionService conversionService, BeanProperty parent, String key)
	{
		super(parent);
		this.conversionService = conversionService;
		this.key = key;
	}


	@Override
	public <T> TypedValue<T> getTypedValue(Object rootBean)
	{
		TypedValue<Map<Object, T>> parentTypedValue = getTypedParentValue(rootBean);
		checkType(parentTypedValue);

		ResolvedType parentType = parentTypedValue.getType();
		Object keyValue = convertKeyValue(parentType);
		ResolvedType resolvedType = determineValueType(parentType);

		Map<Object, T> parentValue = parentTypedValue.getValue();
		if (parentValue == null)
		{
			return new TypedValue<>(resolvedType, null);
		}
		else
		{
			return new TypedValue<>(resolvedType, parentValue.get(keyValue));
		}
	}


	private void checkType(TypedValue<?> parentTypedValue)
	{
		ResolvedType parentType = parentTypedValue.getType();
		if (!parentType.isInstanceOf(Map.class))
		{
			throw new BeanPropertyException(WRONG_PROPERTY_TYPE_ERROR, parentType);
		}
		Object parentValue = parentTypedValue.getValue();
		if (parentValue != null && !(parentValue instanceof Map))
		{
			throw new BeanPropertyException(WRONG_PROPERTY_TYPE_ERROR, parentValue.getClass());
		}
	}


	private Object convertKeyValue(ResolvedType parentType)
	{
		ResolvedType keyType = findTypeParameter(parentType, Map.class, 0);
		return conversionService.convert(Collections.singletonList(key), keyType);
	}


	private ResolvedType determineValueType(ResolvedType parentType)
	{
		return findTypeParameter(parentType, Map.class, 1);
	}


	@Override
	public boolean setValue(Object rootBean, Object value)
	{
		TypedValue<Map<Object, Object>> parentTypedValue = getTypedParentValue(rootBean);
		checkType(parentTypedValue);

		ResolvedType parentType = parentTypedValue.getType();
		Object keyValue = convertKeyValue(parentType);
		ResolvedType resolvedType = determineValueType(parentType);

		if (value != null && !resolvedType.getErasedType().isAssignableFrom(value.getClass()))
		{
			throw new BeanPropertyException(WRONG_VALUE_TYPE_ERROR, value.getClass(), resolvedType);
		}

		Map<Object, Object> parentValue = parentTypedValue.getValue();
		if (parentValue == null) {
			return false;
		}
		parentValue.put(keyValue, value);
        return true;
	}


	@Override
	protected PathBuilder toPathBuilder()
	{
		return parentPathBuilder().addMappedNode(key);
	}
}
