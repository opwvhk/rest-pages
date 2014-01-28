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

import javax.validation.Path;

import com.fasterxml.classmate.ResolvedType;


/**
 * A Java Bean property. Instances are only tied to a bean property, not the bean.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public abstract class BeanProperty
{
	/**
	 * Parent property. If this property is a nested property, the parent property handles all but the last segment.
	 */
	private final BeanProperty parent;


	/**
	 * Create a non-nested (i.e. root) property.
	 */
	protected BeanProperty()
	{
		parent = null;
	}


	/**
	 * Create a nested property.
	 *
	 * @param parent the parent property
	 */
	protected BeanProperty(BeanProperty parent)
	{
		checkParent();
		this.parent = parent;
	}


	private void checkParent()
	{
		if (parent == null)
		{
			throw new IllegalStateException("This property has no parent (it is not a nested property).");
		}
	}


	/**
	 * Get the fully resolved type of the property from a bean.
	 *
	 * @param javaBean the bean to resolve the property type on
	 * @return the fully resolved type of the property
	 */
	public ResolvedType getType(Object javaBean)
	{
		TypedValue<?> typedValue = getTypedValue(javaBean);
		return typedValue.getType();
	}


	/**
	 * Get the property value from a bean.
	 *
	 * @param javaBean the bean to find the property on
	 * @return the property value
	 */
	public <T> T getValue(Object javaBean)
	{
		TypedValue<T> typedValue = getTypedValue(javaBean);
		return typedValue.getValue();
	}


	/**
	 * Set the property value on a bean.
	 *
	 * @param javaBean the bean to find the property on
	 * @param value    the new property value
     * @return {@code true} if the property has been set, {@code false} if not (for example if the property doesn't exist on this bean instance)
     * @throws BeanPropertyException if the property cannot exist on the bean instance/type
	 */
	public abstract boolean setValue(Object javaBean, Object value);


	/**
	 * Get the value of the property with its fully resolved type, including generics if available.
	 *
	 * @param javaBean the bean to find the property on
	 * @return the typed value
	 */
	public abstract <T> TypedValue<T> getTypedValue(Object javaBean);


	/**
	 * Get the types value of the parent of this nested property.
	 *
	 * @param javaBean the bean to find the property on
	 * @return the typed value of the parent
	 * @throws IllegalStateException when this property is not a nested property
	 */
	public <T> TypedValue<T> getTypedParentValue(Object javaBean)
	{
		checkParent();
		return parent.getTypedValue(javaBean);
	}


	/**
	 * Get the path to this property.
	 *
	 * @return the path
	 */
	public Path toPath()
	{
		return toPathBuilder().build();
	}


	/**
	 * Get this property as a path builder.
	 *
	 * @return the path builder
	 */
	protected abstract PathBuilder toPathBuilder();


	/**
	 * Get the parent property as a path builder.
	 *
	 * @return the path builder for the parent property
	 * @throws IllegalStateException when this property is not a nested property
	 */
	protected PathBuilder parentPathBuilder()
	{
		checkParent();
		return parent.toPathBuilder();
	}


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
