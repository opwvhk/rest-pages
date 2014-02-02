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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.validation.Path;

import org.junit.Before;
import org.junit.Test;

import net.sf.opk.beans.conversion.ConversionService;

import static java.util.Collections.singletonList;
import static net.sf.opk.beans.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MapKeyTest extends NestedPropertyTestBase
{
	private Map<Integer, String> map;
	private MapKey mapKey;
	private int keyValue;


	@Before
	public void initialize()
	{
		String key = "42";
		keyValue = 42;

		ConversionService conversionService = mock(ConversionService.class);
		when(conversionService.convert(singletonList(key), resolveType(Integer.class))).thenReturn(keyValue);

		map = new HashMap<>();
		map.put(24, "twentyfour");
		map.put(keyValue, "fortytwo");

		mapKey = new MapKey(conversionService, createParentBean(Map.class, Integer.class, String.class), key);
	}


	@Test(expected = IllegalStateException.class)
	public void testNestedProperty()
	{
		new MapKey(null, null, null);
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongBeanProperty()
	{
		ConversionService conversionService = mock(ConversionService.class);
		new MapKey(conversionService, createParentBean(String.class), "1").getTypedValue(null);
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongBeanType1()
	{
		mapKey.getTypedValue("Not a Map");
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongBeanType2()
	{
		mapKey.setValue("Not a Map", null);
	}


	@Test
	public void testGetValue()
	{
		BeanProperty.TypedValue<String> typedValue = mapKey.getTypedValue(map);
		assertEquals(String.class, typedValue.getType().getErasedType());
		assertEquals(String.class, mapKey.getType(map).getErasedType());
		assertEquals(map.get(keyValue), typedValue.getValue());
		assertEquals(map.get(keyValue), mapKey.getValue(map));
	}


	@Test
	public void testGetNullForNull()
	{
		BeanProperty.TypedValue<Object> typedValue = mapKey.getTypedValue(null);
		assertEquals(String.class, typedValue.getType().getErasedType());
		assertEquals(null, typedValue.getValue());
	}


	@Test
	public void testSetValue1()
	{
		assertTrue(mapKey.setValue(map, null));
		assertNull(map.get(keyValue));
	}


	@Test
	public void testSetValue2()
	{
		String newValue = "the answer";
		assertTrue(mapKey.setValue(map, newValue));
		assertEquals(newValue, map.get(keyValue));
	}


	@Test
	public void testSetValue3()
	{
		assertFalse(mapKey.setValue(null, "the answer"));
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongValueType()
	{
		mapKey.setValue(map, 42L);
	}


	@Test
	public void testPath1()
	{
		Path actual = mapKey.toPath();
		Iterator<Path.Node> iterator = actual.iterator();

		assertTrue(iterator.hasNext());

		Path.Node node = iterator.next();
		assertNull(node.getName());
		assertNull(node.getIndex());
		assertEquals(String.valueOf(keyValue), node.getKey());
		assertTrue(node.isInIterable());

		assertFalse(iterator.hasNext());
	}


	@Test
	public void testPath2()
	{
		Path subProperty = new PathBuilder().addNamedNode("sub").build();

		Path actual = mapKey.prefixTo(subProperty);
		Iterator<Path.Node> iterator = actual.iterator();

		assertTrue(iterator.hasNext());

		Path.Node node = iterator.next();
		assertNull(node.getName());
		assertNull(node.getIndex());
		assertEquals(String.valueOf(keyValue), node.getKey());
		assertTrue(node.isInIterable());

		assertTrue(iterator.hasNext());

		node = iterator.next();
		assertEquals("sub", node.getName());
		assertNull(node.getIndex());
		assertNull(node.getKey());
		assertFalse(node.isInIterable());

		assertFalse(iterator.hasNext());
	}
}
