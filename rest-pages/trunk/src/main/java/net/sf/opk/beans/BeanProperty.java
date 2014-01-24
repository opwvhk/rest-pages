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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.validation.Path;
import javax.validation.Path.Node;

import com.fasterxml.classmate.ResolvedType;


/**
 * A Java Bean property. Instances are only tied to a bean property, not the bean.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public abstract class BeanProperty
{
	/**
	 * Get the value of the property with its fully resolved type, including generics if available.
	 *
	 * @param javaBean the bean to find the property on
	 * @return the typed value
	 */
	public abstract <T> TypedValue<T> getTypedValue(Object javaBean);


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

	/**
	 * A builder for a {@link Path}.
	 */
	protected static class PathBuilder
	{
		private final List<Node> path = new ArrayList<>();


		/**
		 * Create a path with a root node.
		 */
		public PathBuilder()
		{
			path.add(new PathNode());
		}


		/**
		 * Add a named node.
		 *
		 * @param name the name of the node
		 * @return {@code this}
		 */
		public PathBuilder addNamedNode(String name)
		{
			path.add(new PathNode(name));
			return this;
		}


		/**
		 * Add an indexed node.
		 *
		 * @param index the index of the node in its {@link Iterable}
		 * @return {@code this}
		 */
		public PathBuilder addIndexedNode(int index)
		{
			path.add(new PathNode(index));
			return this;
		}


		/**
		 * Add a mapped node.
		 *
		 * @param key the key of the node in its {@link java.util.Map Map}
		 * @return {@code this}
		 */
		public PathBuilder addMappedNode(Object key)
		{
			path.add(new PathNode(key));
			return this;
		}


		public Path build()
		{
			return new Path()
			{
				@Override
				public Iterator<Node> iterator()
				{
					return Collections.unmodifiableList(path).iterator();
				}
			};
		}
	}

	/**
	 * A simple implementation of a {@code Path.Node}.
	 */
	private static class PathNode implements Path.Node
	{
		private final String name;
		private final Integer index;
		private final Object key;


		/**
		 * Create a root node.
		 */
		public PathNode()
		{
			this(null, null, null);
		}


		/**
		 * Create a named node.
		 *
		 * @param name the node name
		 */
		public PathNode(String name)
		{
			this(name, null, null);
		}


		/**
		 * Create an indexed node.
		 *
		 * @param index the index of the node in its {@link Iterable}.
		 */
		public PathNode(int index)
		{
			this(null, index, null);
		}


		/**
		 * Create a mapped node.
		 *
		 * @param key the key for this node in its {@link java.util.Map Map}.
		 */
		public PathNode(Object key)
		{
			this(null, null, key);
		}


		private PathNode(String name, Integer index, Object key)
		{
			this.name = name;
			this.index = index;
			this.key = key;
		}


		@Override
		public String getName()
		{
			return name;
		}


		@Override
		public boolean isInIterable()
		{
			return index != null || key != null;
		}


		@Override
		public Integer getIndex()
		{
			return index;
		}


		@Override
		public Object getKey()
		{
			return key;
		}
	}
}
