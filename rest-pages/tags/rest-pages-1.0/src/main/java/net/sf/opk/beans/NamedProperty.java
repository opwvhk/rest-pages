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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.List;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.rest.util.BeanUtil;

import static net.sf.opk.rest.util.GenericsUtil.resolveReturnType;
import static net.sf.opk.rest.util.GenericsUtil.resolveType;


/**
 * A {@code BeanProperty} describing a named JavaBean property.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class NamedProperty extends NestedBeanProperty
{
	/**
	 * Parent property. If this property is a nested property, the parent property handles all but the last segment.
	 */
	private BeanProperty parent;
	/**
	 * The property name.
	 */
	private String name;


	/**
	 * Create a named property.
	 *
	 * @param parent the propety handling everything but the last path segment
	 * @param name   the property name
	 */
	public NamedProperty(BeanProperty parent, String name)
	{
		this.parent = parent;
		this.name = name;
	}


	@Override
	public <T> TypedValue<T> getTypedValue(Object javaBean)
	{
		// Note 1: returned instances may provide more properties due to subclassing than the signature says.
		TypedValue<Object> parentTypedValue = parent.getTypedValue(javaBean);
		ResolvedType parentType = parentTypedValue.getType();
		Object parentValue = parentTypedValue.getValue();
		Class<?> parentClass = parentValue.getClass();

		PropertyDescriptor propertyDescriptor = BeanUtil.findProperty(parentClass, name);
		Method readMethod = propertyDescriptor.getReadMethod();
		if (readMethod != null)
		{
			ResolvedType resolvedType = resolveReturnType(parentType, readMethod);
			T propertyValue = BeanUtil.invoke(parentValue, readMethod);
			return new TypedValue<>(resolvedType, propertyValue);
		}
		else if (propertyDescriptor instanceof IndexedPropertyDescriptor)
		{
			readMethod = ((IndexedPropertyDescriptor)propertyDescriptor).getIndexedReadMethod();
			ResolvedType resolvedType = resolveType(List.class, resolveReturnType(parentType, readMethod));
			T propertyValue = (T)new IndexedPropertyAsList(parentValue, (IndexedPropertyDescriptor)propertyDescriptor);
			return new TypedValue<>(resolvedType, propertyValue);
		}
		else
		{
			throw new BeanPropertyException("%s has no readable property named %s", parentClass, name);
		}
	}


	@Override
	public void setValue(Object javaBean, Object value)
	{
		TypedValue<Object> parentTypedValue = parent.getTypedValue(javaBean);

		// NOTE: for indexed properties, we'll be the parent of the child BeanProperty that sets the value.

		Class<?> beanType = parentTypedValue.getValue().getClass();
		Method writeMethod = BeanUtil.findProperty(beanType, name).getWriteMethod();
		if (writeMethod == null)
		{
			throw new BeanPropertyException("%s has no writeable property named %s", beanType, name);
		}
		BeanUtil.invoke(parentTypedValue.getValue(), writeMethod, value);
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
