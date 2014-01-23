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
package net.sf.opk.rest.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;


/**
 * A simple, used but untested, implementation of {@code MultivaluedMap}.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class MultiMap<K, V> implements MultivaluedMap<K, V>
{
	private Map<K, List<V>> underlyingMap = new HashMap<>();


	@Override
	public int size()
	{
		return underlyingMap.size();
	}


	@Override
	public boolean isEmpty()
	{
		return underlyingMap.isEmpty();
	}


	@Override
	public void clear()
	{
		underlyingMap.clear();
	}


	@Override
	public Set<K> keySet()
	{
		return underlyingMap.keySet();
	}


	@Override
	public Collection<List<V>> values()
	{
		return underlyingMap.values();
	}


	@Override
	public Set<Entry<K, List<V>>> entrySet()
	{
		return underlyingMap.entrySet();
	}


	@Override
	public boolean containsKey(Object key)
	{
		return underlyingMap.containsKey(key);
		// Templates.
	}


	@Override
	public boolean containsValue(Object value)
	{
		return underlyingMap.containsValue(value);
	}


	@Override
	public List<V> get(Object key)
	{
		return underlyingMap.get(key);
	}


	@Override
	public V getFirst(K k)
	{
		List<V> values = get(k);
		return values == null || values.isEmpty() ? null : values.get(0);
	}


	@Override
	public void putSingle(K k, V v)
	{
		List<V> values = new ArrayList<>();
		underlyingMap.put(k, values);
		values.add(v);
	}


	@Override
	public void add(K k, V v)
	{
		List<V> values = get(k);
		if (values == null)
		{
			values = new ArrayList<>();
			underlyingMap.put(k, values);
		}
		values.add(v);
	}


	@Override
	public List<V> put(K key, List<V> value)
	{
		List<V> values = new ArrayList<>();
		values.addAll(value);
		return underlyingMap.put(key, values);
	}


	@Override
	public void putAll(Map<? extends K, ? extends List<V>> m)
	{
		underlyingMap.putAll(m);
	}


	@Override
	public List<V> remove(Object key)
	{
		return underlyingMap.remove(key);
	}
}
