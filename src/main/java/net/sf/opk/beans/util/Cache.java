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
package net.sf.opk.beans.util;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * A simple cache. Caches values by keys, but using weak references only.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class Cache<K, V>
{
	/**
	 * Internal cache.
	 */
	private final Map<K, Reference<V>> internalCache = new WeakHashMap<>();


	/**
	 * Put a key-value pair in the cache.
	 *
	 * @param key   the key to store
	 * @param value the value to store
	 */
	public void put(K key, V value)
	{
		internalCache.put(key, new WeakReference<>(value));
	}


	/**
	 * Get a value from the cache.
	 *
	 * @param key the key used to store the value
	 * @return the value from the cache, or {@code null} if there is no value
	 */
	public V get(K key)
	{
		V value = null;
		Reference<V> reference = internalCache.get(key);
		if (reference != null)
		{
			value = reference.get();
		}
		return value;
	}
}
