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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.List;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.beans.util.BeanUtil;

import static net.sf.opk.beans.util.GenericsUtil.resolveReturnType;
import static net.sf.opk.beans.util.GenericsUtil.resolveType;


/**
 * A {@code BeanProperty} describing a named JavaBean property.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
class NamedProperty extends BeanProperty
{
	/**
	 * The property name.
	 */
	private final String name;


	/**
	 * Create a named property.
	 *
	 * @param parent the propety handling everything but the last path segment
	 * @param name   the property name
	 */
	public NamedProperty(BeanProperty parent, String name)
	{
		super(parent);
		this.name = name;
	}


	@Override
	public <T> TypedValue<T> getTypedValue(Object rootBean)
	{
		TypedValue<Object> parentTypedValue = getTypedParentValue(rootBean);
		ResolvedType parentType = parentTypedValue.getType();
		Object parentValue = parentTypedValue.getValue();

		PropertyDescriptor propertyDescriptor = BeanUtil.findProperty(parentType.getErasedType(), name);
		Method readMethod = propertyDescriptor.getReadMethod();
		if (readMethod != null)
		{
			ResolvedType resolvedType = resolveReturnType(parentType, readMethod);
			T propertyValue = null;
			if (parentValue != null)
			{
				propertyValue = BeanUtil.invoke(parentValue, readMethod);
			}
			return new TypedValue<>(resolvedType, propertyValue);
		}
		else if (propertyDescriptor instanceof IndexedPropertyDescriptor)
		{
			IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor)propertyDescriptor;
			readMethod = indexedPropertyDescriptor.getIndexedReadMethod();
			ResolvedType resolvedType = resolveType(List.class, resolveReturnType(parentType, readMethod));
			T propertyValue = null;
			if (parentValue != null)
			{
				propertyValue = (T)new IndexedPropertyAsList(parentValue, indexedPropertyDescriptor);
			}
			return new TypedValue<>(resolvedType, propertyValue);
		}
		else
		{
			throw new BeanPropertyException("%s has no readable property named %s", parentType, name);
		}
	}


	@Override
	public boolean setValue(Object rootBean, Object value)
	{
		TypedValue<Object> parentTypedValue = getTypedParentValue(rootBean);

		// NOTE: this method should not be called for indexed properties: getTypedValue(...) returns a list facade for these properties.

		Class<?> beanType = parentTypedValue.getType().getErasedType();
		Method writeMethod = BeanUtil.findProperty(beanType, name).getWriteMethod();
		if (writeMethod == null || writeMethod.getParameterTypes().length > 1)
		{
			throw new BeanPropertyException("%s has no writeable, non-indexed property named %s", beanType, name);
		}

		Object parentValue = parentTypedValue.getValue();
		if (parentValue == null)
		{
			return false;
		}
		BeanUtil.invoke(parentValue, writeMethod, value);
		return true;
	}


	@Override
	protected PathBuilder toPathBuilder()
	{
		return parentPathBuilder().addNamedNode(name);
	}


	private class IndexedPropertyAsList<E> extends AbstractList<E>
	{
		private Object bean;
		private IndexedPropertyDescriptor property;


		private IndexedPropertyAsList(Object bean, IndexedPropertyDescriptor property)
		{
			this.property = property;
			this.bean = bean;
		}


		@Override
		public E get(int index)
		{
			return BeanUtil.invoke(bean, property.getIndexedReadMethod(), index);
		}


		@Override
		public E set(int index, E element)
		{
			return BeanUtil.invoke(bean, property.getIndexedWriteMethod(), index, element);
		}


		@Override
		public int size()
		{
			throw new UnsupportedOperationException("Indexed properties cannot be queried for their size.");
		}
	}
}
