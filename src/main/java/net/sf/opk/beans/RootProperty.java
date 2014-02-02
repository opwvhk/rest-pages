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

import com.fasterxml.classmate.ResolvedType;

import static net.sf.opk.beans.util.GenericsUtil.resolveType;


/**
 * Class to expose a Java Bean as a 'property' via the {@code BeanProperty} interface.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class RootProperty extends BeanProperty
{
	@Override
	public <T> TypedValue<T> getTypedValue(Object rootBean)
	{
		ResolvedType type = getType(rootBean);
		T value = getValue(rootBean);
		return new TypedValue<>(type, value);
	}


	@Override
	public ResolvedType getType(Object rootBean)
	{
		return resolveType(rootBean.getClass());
	}


	@Override
	public <T> T getValue(Object rootBean)
	{
		return (T)rootBean;
	}


	@Override
	public boolean setValue(Object rootBean, Object value)
	{
		throw new BeanPropertyException("Cannot change the root property. Please ensure there are only non-empty " +
		                                "property paths after the prefix.");
	}


	@Override
	protected PathBuilder toPathBuilder()
	{
		return new PathBuilder();
	}
}
