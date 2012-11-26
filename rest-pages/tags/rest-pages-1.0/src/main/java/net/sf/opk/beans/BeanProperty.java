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

import com.fasterxml.classmate.ResolvedType;


/**
 * A Java Bean property. Instances are only tied to a bean property, not the bean.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public interface BeanProperty
{
	/**
	 * Get the value of the property with its fully resolved type, including generics if available.
	 *
	 * @param javaBean the bean to find the property on
	 * @return the typed value
	 */
	<T> TypedValue<T> getTypedValue(Object javaBean);

	/**
	 * Get the fully resolved type of the property from a bean.
	 *
	 * @param javaBean the bean to resolve the property type on
	 * @return the fully resolved type of the property
	 */
	ResolvedType getType(Object javaBean);

	/**
	 * Get the property value from a bean.
	 *
	 * @param javaBean the bean to find the property on
	 * @return the property value
	 */
	<T> T getValue(Object javaBean);

	/**
	 * Set the property value on a bean.
	 *
	 * @param javaBean the bean to find the property on
	 * @param value    the new property value
	 */
	void setValue(Object javaBean, Object value);

	/**
	 * A typed value to pass along a value (even {@code null}) along with its non-erased type.
	 */
	public static class TypedValue<T>
	{
		private final ResolvedType type;
		private final T value;


		public TypedValue(ResolvedType type, T value)
		{
			this.type = type;
			this.value = value;
		}


		public ResolvedType getType()
		{
			return type;
		}


		public T getValue()
		{
			return value;
		}
	}
}
