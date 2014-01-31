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


/**
 * A simple implementation of a {@code Path.Node}.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
class PathNode implements Path.Node
{
	private final String name;
	private final Integer index;
	private final Object key;


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


	PathNode(String name, Integer index, Object key)
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
