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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.validation.Path;

import org.junit.Before;
import org.junit.Test;

import static net.sf.opk.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class NamedPropertyTest extends NestedPropertyTestBase
{
	private DummyBean bean;
	private NamedProperty namedPropertyRW;
	private NamedProperty namedPropertyR;
	private NamedProperty namedPropertyW;
	private NamedProperty namedPropertyI1;
	private NamedProperty namedPropertyI2;


	@Before
	public void initialize()
	{
		bean = new DummyBean();
		bean.setName("John Doe");
		bean.setIndexed1(0, 'a');
		bean.setIndexed1(1, 'b');
		bean.setIndexed2(new Character[]{'a', 'b'});

		BeanProperty parentBean = createParentBean(DummyBean.class);
		namedPropertyRW = new NamedProperty(parentBean, "name");
		namedPropertyR = new NamedProperty(parentBean, "readOnly");
		namedPropertyW = new NamedProperty(parentBean, "writeOnly");
		namedPropertyI1 = new NamedProperty(parentBean, "indexed1");
		namedPropertyI2 = new NamedProperty(parentBean, "indexed2");
	}


	@Test(expected = IllegalArgumentException.class)
	public void testMissingProperty()
	{
		namedPropertyRW.getTypedValue("A String has no property called name");
	}


	@Test
	public void testGetTypedValueRW()
	{
		BeanProperty.TypedValue<String> typedValue = namedPropertyRW.getTypedValue(bean);
		assertEquals(String.class, typedValue.getType().getErasedType());
		assertEquals(bean.getName(), typedValue.getValue());
		assertEquals(typedValue.getType(), namedPropertyRW.getType(bean));
		assertEquals(typedValue.getValue(), namedPropertyRW.getValue(bean));
	}


	@Test
	public void testGetTypedValueR()
	{
		BeanProperty.TypedValue<Integer> typedValue = namedPropertyR.getTypedValue(bean);
		assertEquals(Integer.class, typedValue.getType().getErasedType());
		assertEquals(bean.getReadOnly(), typedValue.getValue());
		assertEquals(typedValue.getType(), namedPropertyR.getType(bean));
		assertEquals(typedValue.getValue(), namedPropertyR.getValue(bean));
	}


	@Test(expected = BeanPropertyException.class)
	public void testGetTypedValueW()
	{
		namedPropertyW.getTypedValue(bean);
	}


	@Test
	public void testGetTypedValueI1()
	{
		BeanProperty.TypedValue<List<Character>> typedValue = namedPropertyI1.getTypedValue(bean);
		assertEquals(resolveType(List.class, Character.class), typedValue.getType());
		List<Character> value1 = typedValue.getValue();
		assertEquals((Character)'a', value1.get(0));
		assertEquals((Character)'b', value1.get(1));
		assertEquals(typedValue.getType(), namedPropertyI1.getType(bean));
		List<Character> value2 = namedPropertyI1.getValue(bean);
		assertEquals((Character)'a', value2.get(0));
		assertEquals((Character)'b', value2.get(1));
	}


	@Test
	public void testGetTypedValueI2()
	{
		BeanProperty.TypedValue<Character[]> typedValue = namedPropertyI2.getTypedValue(bean);
		assertEquals(resolveType(Character[].class), typedValue.getType());
		Character[] value1 = typedValue.getValue();
		assertEquals((Character)'a', value1[0]);
		assertEquals((Character)'b', value1[1]);
		assertEquals(typedValue.getType(), namedPropertyI2.getType(bean));
		Character[] value2 = namedPropertyI2.getValue(bean);
		assertEquals((Character)'a', value2[0]);
		assertEquals((Character)'b', value2[1]);
	}


	@Test
	public void testGetNullForNullRW()
	{
		BeanProperty.TypedValue<Object> typedValue = namedPropertyRW.getTypedValue(null);
		assertEquals(String.class, typedValue.getType().getErasedType());
		assertEquals(null, typedValue.getValue());
	}


	@Test
	public void testGetNullForNullR()
	{
		BeanProperty.TypedValue<Object> typedValue = namedPropertyR.getTypedValue(null);
		assertEquals(Integer.class, typedValue.getType().getErasedType());
		assertEquals(null, typedValue.getValue());
	}


	@Test
	public void testGetNullForNullI1()
	{
		BeanProperty.TypedValue<Object> typedValue = namedPropertyI1.getTypedValue(null);
		assertEquals(List.class, typedValue.getType().getErasedType());
		assertEquals(null, typedValue.getValue());
	}


	@Test
	public void testGetNullForNullI2()
	{
		BeanProperty.TypedValue<Object> typedValue = namedPropertyI2.getTypedValue(null);
		assertEquals(Character[].class, typedValue.getType().getErasedType());
		assertEquals(null, typedValue.getValue());
	}


	@Test
	public void testSetTypedValueRW()
	{
		String newValue = "new name";
		assertTrue(namedPropertyRW.setValue(bean, newValue));
		assertEquals(newValue, bean.getName());
	}


	@Test(expected = BeanPropertyException.class)
	public void testSetTypedValueR()
	{
		namedPropertyR.setValue(bean, null);
	}


	@Test
	public void testSetTypedValueW()
	{
		Boolean newValue = true;
		String oldBeanString = bean.toString();

		assertTrue(namedPropertyW.setValue(bean, newValue));
		assertEquals(oldBeanString.replace("false", "true"), bean.toString());
	}


	@Test(expected = BeanPropertyException.class)
	public void testSetTypedValueI1()
	{
		assertTrue(namedPropertyI1.setValue(bean, null));
	}


	@Test
	public void testSetTypedValueOnNull()
	{
		assertFalse(namedPropertyRW.setValue(null, null));
	}


	@Test
	public void testIndexedResultProperties1()
	{
		BeanProperty.TypedValue<List<Character>> typedValue = namedPropertyI1.getTypedValue(bean);
		List<Character> value = typedValue.getValue();

		value.set(0, 'b');
		assertEquals((Character)'b', bean.getIndexed1(0));
	}


	@Test(expected = UnsupportedOperationException.class)
	public void testIndexedResultProperties2()
	{
		BeanProperty.TypedValue<List<Character>> typedValue = namedPropertyI1.getTypedValue(bean);
		typedValue.getValue().size();
	}


	@Test
	public void testSetTypedValueI2()
	{
		Character[] newValue = {'c', 'd'};
		assertTrue(namedPropertyI2.setValue(bean, newValue));
		assertArrayEquals(newValue, bean.getIndexed2());
	}


	@Test
	public void testPath()
	{
		Path actual = namedPropertyRW.toPath();
		Iterator<Path.Node> iterator = actual.iterator();

		assertTrue(iterator.hasNext());

		Path.Node node = iterator.next();
		assertNull(node.getName());
		assertNull(node.getIndex());
		assertNull(node.getKey());
		assertFalse(node.isInIterable());

		assertTrue(iterator.hasNext());

		node = iterator.next();
		assertEquals("name", node.getName());
		assertNull(node.getIndex());
		assertNull(node.getKey());
		assertFalse(node.isInIterable());

		assertFalse(iterator.hasNext());
	}


	/**
	 * Dummy bean for {@link NamedPropertyTest}.
	 *
	 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
	 */
	public static class DummyBean
	{
		private String name;
		private Integer readOnly = 42;
		private Boolean writeOnly = false;
		private Character[] indexed1 = new Character[2];
		private Character[] indexed2 = new Character[2];


		public String getName()
		{
			return name;
		}


		public void setName(String name)
		{
			this.name = name;
		}


		public Integer getReadOnly()
		{
			return readOnly;
		}


		public void setWriteOnly(Boolean writeOnly)
		{
			this.writeOnly = writeOnly;
		}


		public Character getIndexed1(int index)
		{
			return indexed1[index];
		}


		public void setIndexed1(int index, Character value)
		{
			this.indexed1[index] = value;
		}


		public Character[] getIndexed2()
		{
			return indexed2;
		}


		public void setIndexed2(Character[] indexed2)
		{
			this.indexed2 = indexed2;
		}


		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("DummyBean");
			sb.append("{name='").append(name).append('\'');
			sb.append(", readOnly='").append(readOnly).append('\'');
			sb.append(", writeOnly='").append(writeOnly).append('\'');
			sb.append(", indexed1='").append(Arrays.<Character>asList(indexed1)).append('\'');
			sb.append(", indexed2='").append(Arrays.<Character>asList(indexed2)).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}
}
