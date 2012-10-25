package net.sf.opk.rest.util;

import java.util.Comparator;


/**
 * Comparator that orders objects according to their priority (highest first). By default, all objects have priority 0.
 * Objects that implement the {@link Prioritized} interface can change this.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class PriorityComparator implements Comparator<Object>
{
	@Override
	public int compare(Object o1, Object o2)
	{
		int index1 = Prioritized.DEFAULT_PRIORITY;
		if (o1 instanceof Prioritized)
		{
			index1 = ((Prioritized)o1).getPriority();
		}

		int index2 = Prioritized.DEFAULT_PRIORITY;
		if (o2 instanceof Prioritized)
		{
			index2 = ((Prioritized)o2).getPriority();
		}

		// Reverse the order: a higher priority should be sorted first.
		return -Integer.compare(index1, index2);
	}
}
