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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.sf.opk.rest.forms.conversion.ConversionService;

import static java.util.Collections.singletonList;
import static net.sf.opk.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PropertyParserTest
{
	private static final String KEY1 = "KEY1";
	private static final String KEY2 = "KEY2";
	private static final String INDEX1_STRING = "0";
	private static final String INDEX2_STRING = "1";
	private static final int INDEX1 = 0;
	private static final int INDEX2 = 1;
	private PropertyParser propertyParser;
	private DummyBean bean;


	@Before
	public void initialize()
	{
		ConversionService conversionService = mock(ConversionService.class);
		when(conversionService.convert(singletonList(KEY1), resolveType(String.class))).thenReturn(KEY1);
		when(conversionService.convert(singletonList(KEY2), resolveType(String.class))).thenReturn(KEY2);
		when(conversionService.convert(singletonList(INDEX1_STRING), resolveType(int.class))).thenReturn(INDEX1);
		when(conversionService.convert(singletonList(INDEX2_STRING), resolveType(int.class))).thenReturn(INDEX2);

		propertyParser = new PropertyParser(conversionService);

		bean = new DummyBean();
		bean.setParent(new DummyBean());
	}


	@Test
	public void testSuccessfulParsing1()
	{
		String value1 = "foo";
		String value2 = "bar";
		BeanProperty property = propertyParser.parse("parent.name");

		bean.getParent().setName(value1);
		assertEquals(value1, property.getValue(bean));

		property.setValue(bean, value2);
		assertEquals(value2, bean.getParent().getName());
	}


	@Test
	public void testSuccessfulParsing2()
	{
		String value1 = "foo";
		String value2 = "bar";
		BeanProperty property = propertyParser.parse("parent.aliases[" + INDEX1_STRING + "]");

		bean.getParent().setAliases(new ArrayList<String>());
		bean.getParent().getAliases().add(value1);
		bean.getParent().getAliases().add(value1);
		assertEquals(value1, property.getValue(bean));

		property.setValue(bean, value2);
		assertEquals(value2, bean.getParent().getAliases().get(INDEX1));
		assertEquals(value1, bean.getParent().getAliases().get(INDEX2));
	}


	@Test
	public void testSuccessfulParsing3()
	{
		int value1 = 123;
		int value2 = 456;
		BeanProperty property = propertyParser.parse("parent.indexed1[" + INDEX1_STRING + "]");

		bean.getParent().setIndexed1(new int[]{value1, value1});
		assertEquals(value1, property.getValue(bean));

		property.setValue(bean, value2);
		assertEquals(value2, bean.getParent().getIndexed1()[INDEX1]);
		assertEquals(value1, bean.getParent().getIndexed1()[1]);
	}


	@Test
	public void testSuccessfulParsing4()
	{
		int value1 = 123;
		int value2 = 456;
		BeanProperty property = propertyParser.parse("parent.indexed2[" + INDEX1_STRING + "]");

		bean.getParent().setIndexed2(INDEX1, value1);
		bean.getParent().setIndexed2(INDEX2, value1);
		assertEquals(value1, property.getValue(bean));

		property.setValue(bean, value2);
		assertEquals(value2, bean.getParent().getIndexed2(INDEX1));
		assertEquals(value1, bean.getParent().getIndexed2(INDEX2));
	}


	@Test
	public void testSuccessfulParsing5()
	{
		Integer value1 = 123;
		Integer value2 = 456;
		BeanProperty property = propertyParser.parse("parent.variables[" + KEY1 + "]");

		bean.getParent().setVariables(new HashMap<String, Integer>());
		bean.getParent().getVariables().put(KEY1, value1);
		bean.getParent().getVariables().put(KEY2, value1);
		assertEquals(value1, property.getValue(bean));

		property.setValue(bean, value2);
		assertEquals(value2, bean.getParent().getVariables().get(KEY1));
		assertEquals(value1, bean.getParent().getVariables().get(KEY2));
	}


	@Test
	public void testSuccessfulParsing6()
	{
		Integer value1 = 123;
		Integer value2 = 456;
		BeanProperty property = propertyParser.parse("parent.variables['" + KEY1 + "']");

		bean.getParent().setVariables(new HashMap<String, Integer>());
		bean.getParent().getVariables().put(KEY1, value1);
		bean.getParent().getVariables().put(KEY2, value1);
		assertEquals(value1, property.getValue(bean));

		property.setValue(bean, value2);
		assertEquals(value2, bean.getParent().getVariables().get(KEY1));
		assertEquals(value1, bean.getParent().getVariables().get(KEY2));
	}


	@Test
	public void testSuccessfulParsing7()
	{
		Integer value1 = 123;
		Integer value2 = 456;
		BeanProperty property = propertyParser.parse("parent.variables[\"" + KEY1 + "\"]");

		bean.getParent().setVariables(new HashMap<String, Integer>());
		bean.getParent().getVariables().put(KEY1, value1);
		bean.getParent().getVariables().put(KEY2, value1);
		assertEquals(value1, property.getValue(bean));

		property.setValue(bean, value2);
		assertEquals(value2, bean.getParent().getVariables().get(KEY1));
		assertEquals(value1, bean.getParent().getVariables().get(KEY2));
	}


	@Test
	public void testParsedPropertyCache()
	{
		long time1 = System.nanoTime();
		BeanProperty property1 = propertyParser.parse("parent.aliases");
		long time2 = System.nanoTime();
		BeanProperty property2 = propertyParser.parse("parent.aliases");
		long time3 = System.nanoTime();

		assertSame(property1, property2);
		long parsing1 = time2 - time1;
		long parsing2 = time3 - time2;
		assertTrue(parsing1 > parsing2);
	}


	@Test(expected = BeanPropertyException.class)
	public void testParseError1()
	{
		propertyParser.parse(null);
	}


	@Test(expected = BeanPropertyException.class)
	public void testParseError2()
	{
		propertyParser.parse("");
	}


	@Test(expected = BeanPropertyException.class)
	public void testParseError3()
	{
		propertyParser.parse(".name");
	}


	@Test(expected = BeanPropertyException.class)
	public void testParseError4()
	{
		propertyParser.parse("aliases.4");
	}


	public static class DummyBean extends DummyBeanSuper
	{
		private DummyBean parent;
		private List<String> aliases;
		private int[] indexed1;
		private int[] indexed2 = new int[2];


		public DummyBean getParent()
		{
			return parent;
		}


		public void setParent(DummyBean parent)
		{
			this.parent = parent;
		}


		public List<String> getAliases()
		{
			return aliases;
		}


		public void setAliases(List<String> aliases)
		{
			this.aliases = aliases;
		}


		public int[] getIndexed1()
		{
			return indexed1;
		}


		public void setIndexed1(int[] indexed1)
		{
			this.indexed1 = indexed1;
		}


		public int getIndexed2(int index)
		{
			return indexed2[index];
		}


		public void setIndexed2(int index, int value)
		{
			this.indexed2[index] = value;
		}
	}

	public static class DummyBeanSuper
	{
		private String name;
		private Map<String, Integer> variables;


		public String getName()
		{
			return name;
		}


		public void setName(String name)
		{
			this.name = name;
		}


		public Map<String, Integer> getVariables()
		{
			return variables;
		}


		public void setVariables(Map<String, Integer> variables)
		{
			this.variables = variables;
		}
	}
}
