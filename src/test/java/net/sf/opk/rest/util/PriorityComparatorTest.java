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
