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
public abstract class NestedBeanProperty implements BeanProperty
{
	@Override
	public ResolvedType getType(Object javaBean)
	{
		TypedValue<?> typedValue = getTypedValue(javaBean);
		return typedValue.getType();
	}


	@Override
	public <T> T getValue(Object javaBean)
	{
		TypedValue<T> typedValue = getTypedValue(javaBean);
		return typedValue.getValue();
	}
}
