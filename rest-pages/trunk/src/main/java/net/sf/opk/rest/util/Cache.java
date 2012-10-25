package net.sf.opk.rest.util;

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
