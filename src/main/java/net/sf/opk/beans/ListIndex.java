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

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.classmate.ResolvedType;

import static net.sf.opk.rest.util.GenericsUtil.findTypeParameter;
import static net.sf.opk.rest.util.GenericsUtil.resolveType;


/**
 * A {@code BeanProperty} describing an array or {@link List} index.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class ListIndex extends NestedBeanProperty
{
	/**
	 * Error message to throw when unsupported beans are given to {@link #getValue(Object)} and {@link #setValue(Object,
	 * Object)}.
	 */
	private static final String WRONG_PROPERTY_TYPE_ERROR =
			"Cannot access indexed property: %s does not " + "implement java.util.List, nor is it an array.";
	/**
	 * Error message to throw when unsupported values are given to {@link #setValue(Object, Object)}.
	 */
	private static final String WRONG_VALUE_TYPE_ERROR = "Cannot set indexed property: %s is not a %s.";
	private static final Map<ResolvedType, Class<?>> BOXED_TYPES;
	static {
		Map<ResolvedType, Class<?>> boxedTypes = new HashMap<>();
		boxedTypes.put(resolveType(Byte.TYPE), Byte.class);
		boxedTypes.put(resolveType(Short.TYPE), Short.class);
		boxedTypes.put(resolveType(Character.TYPE), Character.class);
		boxedTypes.put(resolveType(Integer.TYPE), Integer.class);
		boxedTypes.put(resolveType(Long.TYPE), Long.class);
		boxedTypes.put(resolveType(Float.TYPE), Float.class);
		boxedTypes.put(resolveType(Double.TYPE), Double.class);
		boxedTypes.put(resolveType(Boolean.TYPE), Boolean.class);
		boxedTypes.put(resolveType(Character.TYPE), Character.class);
		BOXED_TYPES = Collections.unmodifiableMap(boxedTypes);
	}
	/**
	 * Parent property. This property is a nested property, so the parent property handles all but the last segment.
	 */
	private BeanProperty parent;
	/**
	 * The index to represent.
	 */
	private int index;


	/**
	 * Create a list index.
	 *
	 * @param parent the propety handling everything but the last path segment
	 * @param index  the index to represent
	 */
	public ListIndex(BeanProperty parent, int index)
	{
		this.parent = parent;
		this.index = index;
	}


	@Override
	public <T> TypedValue<T> getTypedValue(Object javaBean)
	{
		TypedValue<Object> parentTypedValue = parent.getTypedValue(javaBean);
		ResolvedType parentType = parentTypedValue.getType();

		ResolvedType resolvedType = determineElementType(parentType);

		Object parentValue = parentTypedValue.getValue();
		if (parentType.isArray())
		{
			T value = (T)Array.get(parentValue, index);
			return new TypedValue<>(resolvedType, value);
		}
		else
		{
			T value = (T)((List<Object>)parentValue).get(index);
			return new TypedValue<>(resolvedType, value);
		}
	}


	/**
	 * Determine the element type of the parent bean. Throws an exception if the parent bean is not a List or array.
	 *
	 * @param parentType the type of the parent bean
	 * @return the element type
	 */
	protected ResolvedType determineElementType(ResolvedType parentType)
	{
		if (parentType.isArray())
		{
			return parentType.getArrayElementType();
		}
		else if (parentType.isInstanceOf(List.class))
		{
			return findTypeParameter(parentType, List.class, 0);
		}
		else
		{
			throw new BeanPropertyException(WRONG_PROPERTY_TYPE_ERROR, parentType);
		}
	}


	@Override
	public void setValue(Object javaBean, Object value)
	{
		TypedValue<Object> parentTypedValue = parent.getTypedValue(javaBean);
		ResolvedType parentType = parentTypedValue.getType();

		ResolvedType elementType = determineElementType(parentType);
		if (value != null && !elementType.getErasedType().isAssignableFrom(value.getClass())
		    && !(elementType.isPrimitive() && BOXED_TYPES.get(elementType).equals(value.getClass())))
		{
			throw new BeanPropertyException(WRONG_VALUE_TYPE_ERROR, value.getClass(), elementType);
		}

		Object parentValue = parentTypedValue.getValue();
		if (parentType.isArray())
		{
			Array.set(parentValue, index, value);
		}
		else
		{
			((List<Object>)parentValue).set(index, value);
		}
	}
}
