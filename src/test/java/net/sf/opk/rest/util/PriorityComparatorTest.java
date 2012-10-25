package net.sf.opk.rest.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class PriorityComparatorTest
{
	@Test
	public void testSortOrder()
	{
		Object first = new PrioritizedObject(1);
		Object second = new PrioritizedObject(0);
		Object third = new Object();
		Object fourth = "last but one";
		Object fifth = new PrioritizedObject(-1);

		List<Object> input = Arrays.asList(second, fifth, first, third, fourth);
		PriorityComparator comparator = new PriorityComparator();
		Collections.sort(input, comparator);

		assertEquals(Arrays.asList(first, second, third, fourth, fifth), input);
	}


	private class PrioritizedObject implements Prioritized
	{
		private int priority;


		PrioritizedObject(int priority)
		{
			this.priority = priority;
		}


		@Override
		public int getPriority()
		{
			return priority;
		}
	}
}
