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
import javax.validation.Path;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class RootPropertyTest
{
	RootProperty rootProperty;


	@Before
	public void initialize()
	{
		rootProperty = new RootProperty();
	}


	@Test
	public void testTypedValue()
	{
		Object bean = 42L;

		BeanProperty.TypedValue<Object> typedValue = rootProperty.getTypedValue(bean);
		assertEquals(Long.class, typedValue.getType().getErasedType());
		assertEquals(bean, typedValue.getValue());
		assertEquals(typedValue.getType(), rootProperty.getType(bean));
		assertEquals(typedValue.getValue(), rootProperty.getValue(bean));
	}


	@Test(expected = BeanPropertyException.class)
	public void testSetValue()
	{
		rootProperty.setValue(new Object(), null);
	}


	@Test
	public void testPath1()
	{
		Path actual = rootProperty.toPath();
		Iterator<Path.Node> iterator = actual.iterator();

		assertFalse(iterator.hasNext());
	}


	@Test
	public void testPath2()
	{
		BeanProperty prefixProperty = mock(BeanProperty.class);
		PathBuilder prefixPathBuilder = new PathBuilder();
		prefixPathBuilder.addNamedNode("prefix");
		when(prefixProperty.toPathBuilder(null)).thenReturn(prefixPathBuilder);

		Path actual = rootProperty.toPath(prefixProperty);
		Iterator<Path.Node> iterator = actual.iterator();

		assertTrue(iterator.hasNext());

		Path.Node node = iterator.next();
		assertEquals("prefix", node.getName());
		assertNull(node.getIndex());
		assertNull(node.getKey());
		assertFalse(node.isInIterable());

		assertFalse(iterator.hasNext());
	}
}
