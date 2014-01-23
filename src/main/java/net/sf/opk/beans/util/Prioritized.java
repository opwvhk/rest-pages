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
package net.sf.opk.beans.util;

/**
 * Interface that allows objects to define an optional, extrinsic priority. Used in combination with {@link
 * PriorityComparator} to sort objects. By default, objects have priority {@link #DEFAULT_PRIORITY}. This interface
 * allows objects to put themselves ahead or behind others by returning a different value.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public interface Prioritized
{
	/**
	 * The priority to be used the objects that do not implement this interface.
	 */
	static final int DEFAULT_PRIORITY = 0;

	/**
	 * Get the priority of the object. Higher values denote a higher priority (and vice versa). The value {@link
	 * #DEFAULT_PRIORITY} is neutral, and is also assigned to objects that do not implement this interface.
	 *
	 * @return the priority of the object
	 */
	int getPriority();
}
