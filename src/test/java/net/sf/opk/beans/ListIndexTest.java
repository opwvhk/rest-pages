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

import java.util.Iterator;
import java.util.List;
import javax.validation.Path;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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


	@Test(expected = IllegalStateException.class)
	public void testNestedProperty()
	{
		new ListIndex(null, 1);
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongBeanProperty()
	{
		new ListIndex(createParentBean(String.class), 1).getTypedValue(null);
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
	public void testGetNullForNull1()
	{
		BeanProperty.TypedValue<Object> typedValue = arrayIndex.getTypedValue(null);
		assertEquals(long.class, typedValue.getType().getErasedType());
		assertEquals(null, typedValue.getValue());
	}


	@Test
	public void testGetNullForNull2()
	{
		BeanProperty.TypedValue<Object> typedValue = listIndex.getTypedValue(null);
		assertEquals(String.class, typedValue.getType().getErasedType());
		assertEquals(null, typedValue.getValue());
	}


	@Test
	public void testSetArrayValue()
	{
		long newValue = 123;
		assertTrue(arrayIndex.setValue(array, newValue));
		assertEquals(newValue, array[1]);
	}


	@Test
	public void testSetListValue1()
	{
		assertTrue(listIndex.setValue(list, null));
		assertNull(list.get(1));
	}


	@Test
	public void testSetListValue2()
	{
		String newValue = "foo";
		assertTrue(listIndex.setValue(list, newValue));
		assertEquals(newValue, list.get(1));
	}


	@Test
	public void testSetListValue3()
	{
		assertFalse(listIndex.setValue(null, "foo"));
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


	@Test
	public void testPath1()
	{
		Path actual = listIndex.toPath();
		Iterator<Path.Node> iterator = actual.iterator();

		assertTrue(iterator.hasNext());

		Path.Node node = iterator.next();
		assertNull(node.getName());
		assertEquals(Integer.valueOf(1), node.getIndex());
		assertNull(node.getKey());
		assertTrue(node.isInIterable());

		assertFalse(iterator.hasNext());
	}


	@Test
	public void testPath2()
	{
		BeanProperty prefixProperty = mock(BeanProperty.class);
		PathBuilder prefixPathBuilder = new PathBuilder();
		prefixPathBuilder.addNamedNode("prefix");
		when(prefixProperty.toPathBuilder(null)).thenReturn(prefixPathBuilder);

		Path actual = arrayIndex.toPath(prefixProperty);
		Iterator<Path.Node> iterator = actual.iterator();

		assertTrue(iterator.hasNext());

		Path.Node node = iterator.next();
		assertEquals("prefix", node.getName());
		assertNull(node.getIndex());
		assertNull(node.getKey());
		assertFalse(node.isInIterable());

		assertTrue(iterator.hasNext());

		node = iterator.next();
		assertNull(node.getName());
		assertEquals(Integer.valueOf(1), node.getIndex());
		assertNull(node.getKey());
		assertTrue(node.isInIterable());

		assertFalse(iterator.hasNext());
	}
}
