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
package net.sf.opk.beans;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class ListIndexTest extends NestedPropertyTestBase
{
	private long[] array;
	private List<String> list;
	private ListIndex arrayIndex;
	private ListIndex listIndex;


	@Before
	public void initialize()
	{
		array = new long[]{24L, 42L};
		list = asList("one", "two");

		arrayIndex = new ListIndex(createParentBean(long[].class), 1);
		listIndex = new ListIndex(createParentBean(List.class, String.class), 1);
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongBeanType()
	{
		arrayIndex.getTypedValue("Not an array, nor a List");
	}


	@Test
	public void testGetArrayValue()
	{
		BeanProperty.TypedValue<Object> typedValue = arrayIndex.getTypedValue(array);
		assertEquals(Long.TYPE, typedValue.getType().getErasedType());
		assertEquals(Long.TYPE, arrayIndex.getType(array).getErasedType());
		assertEquals(array[1], typedValue.getValue());
		assertEquals(array[1], arrayIndex.getValue(array));
	}


	@Test
	public void testGetListValue()
	{
		BeanProperty.TypedValue<Object> typedValue = listIndex.getTypedValue(list);
		assertEquals(String.class, typedValue.getType().getErasedType());
		assertEquals(String.class, listIndex.getType(list).getErasedType());
		assertEquals(list.get(1), typedValue.getValue());
		assertEquals(list.get(1), listIndex.getValue(list));
	}


	@Test
	public void testSetArrayValue()
	{
		long newValue = 123;
		arrayIndex.setValue(array, newValue);
		assertEquals(newValue, array[1]);
	}


	@Test
	public void testSetListValue1()
	{
		listIndex.setValue(list, null);
		assertNull(list.get(1));
	}


	@Test
	public void testSetListValue2()
	{
		String newValue = "foo";
		listIndex.setValue(list, newValue);
		assertEquals(newValue, list.get(1));
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongValueType1()
	{
		arrayIndex.setValue(array, true);
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongValueType2()
	{
		listIndex.setValue(list, 42L);
	}
}
