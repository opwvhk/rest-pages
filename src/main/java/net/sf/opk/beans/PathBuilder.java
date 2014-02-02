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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.validation.Path;


/**
 * A builder for a {@link Path}.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
class PathBuilder
{
	private final ArrayList<Path.Node> path = new ArrayList<>();


	/**
	 * Add a named node.
	 *
	 * @param name the name of the node
	 * @return {@code this}
	 */
	public PathBuilder addNamedNode(String name)
	{
		return addNode(new PathNode(name));
	}


	/**
	 * Add an indexed node.
	 *
	 * @param index the index of the node in its {@link Iterable}
	 * @return {@code this}
	 */
	public PathBuilder addIndexedNode(int index)
	{
		return addNode(new PathNode(index));
	}


	/**
	 * Add a mapped node.
	 *
	 * @param key the key of the node in its {@link java.util.Map Map}
	 * @return {@code this}
	 */
	public PathBuilder addMappedNode(Object key)
	{
		return addNode(new PathNode(key));
	}


	/**
	 * Add a node.
	 *
	 * @param node the node to add
	 * @return {@code this}
	 */
	public PathBuilder addNode(Path.Node node)
	{
		path.add(node);
		return this;
	}


	/**
	 * Build the current path.
	 *
	 * @return the path
	 */
	public Path build()
	{
		return new Path()
		{
			@Override
			public Iterator<Node> iterator()
			{
				return Collections.unmodifiableList((List<Node>)path.clone()).iterator();
			}
		};
	}
}
