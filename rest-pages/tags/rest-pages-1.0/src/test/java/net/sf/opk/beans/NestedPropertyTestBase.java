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

import static net.sf.opk.rest.util.GenericsUtil.resolveType;


public class NestedPropertyTestBase
{
	protected BeanProperty createParentBean(Class<?> erasedType, Class<?>... typeParameters)
	{
		return new DummyBeanProperty(resolveType(erasedType, typeParameters));
	}


	private class DummyBeanProperty implements BeanProperty
	{
		ResolvedType type;


		private DummyBeanProperty(ResolvedType type)
		{
			this.type = type;
		}


		@Override
		public <T> TypedValue<T> getTypedValue(Object javaBean)
		{
			T value = getValue(javaBean);
			return new TypedValue<>(getType(javaBean), value);
		}


		@Override
		public ResolvedType getType(Object javaBean)
		{
			Class<?> beanClass = javaBean.getClass();
			return type.getErasedType().isAssignableFrom(beanClass) ? type : resolveType(beanClass);
		}


		@Override
		public <T> T getValue(Object javaBean)
		{
			return (T)javaBean;
		}


		@Override
		public void setValue(Object javaBean, Object value)
		{
			throw new BeanPropertyException("Error in test.");
		}
	}
}
