package net.sf.opk.rest.util;

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