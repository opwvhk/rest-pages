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
package net.sf.opk.beans.converters;

import java.util.Collections;
import java.util.List;

import com.fasterxml.classmate.ResolvedType;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static net.sf.opk.beans.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class ConverterByValueOfTest
{
	private ConverterByValueOf converter;


	@Before
	public void initialize()
	{
		converter = new ConverterByValueOf();
	}


	@Test
	public void testPriority() throws Exception
	{
		assertEquals(Integer.MIN_VALUE, converter.getPriority());
	}


	@Test
	public void testImpossibleConversion1() throws Exception
	{
		assertFalse(converter.canConvertTo(resolveType(List.class)));
	}


	@Test
	public void testImpossibleConversion2() throws Exception
	{
		assertFalse(converter.canConvertTo(resolveType(InvalidBean1.class)));
	}


	@Test
	public void testImpossibleConversion3() throws Exception
	{
		assertFalse(converter.canConvertTo(resolveType(InvalidBean2.class)));
	}


	@Test(expected = ConversionException.class)
	public void testImpossibleConversion4() throws Exception
	{
		converter.convertTo(resolveType(InvalidBean2.class), "");
	}


	@Test
	public void testConversion1() throws Exception
	{
		ResolvedType resolvedType = resolveType(Long.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertNull(converter.convertTo(resolvedType, Collections.<String>emptyList()));
	}


	@Test
	public void testConversion2() throws Exception
	{
		ResolvedType resolvedType = resolveType(Long.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertNull(converter.convertTo(resolvedType, asList(null, "456")));
	}


	@Test
	public void testConversion3() throws Exception
	{
		ResolvedType resolvedType = resolveType(Long.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertNull(converter.convertTo(resolvedType, asList("", "456")));
	}


	@Test
	public void testConversion4() throws Exception
	{
		ResolvedType resolvedType = resolveType(Long.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertEquals(123L, converter.convertTo(resolvedType, singletonList("123")));
	}


	@Test
	public void testParentSignatureCheck() throws Exception
	{
		assertFalse(converter.isSingleStringParameter(new Class<?>[]{Object.class}));
		assertFalse(converter.isSingleStringParameter(new Class<?>[]{String.class, String.class}));
		assertTrue(converter.isSingleStringParameter(new Class<?>[]{String.class}));
	}


	@SuppressWarnings("ALL")
	public static class InvalidBean1
	{
		public String toString()
		{
			return "InvalidBean1";
		}


		public static void foo()
		{
			// Nothing to do.
		}


		public static String valueOf(String value)
		{
			return null;
		}
	}

	@SuppressWarnings("ALL")
	public static class InvalidBean2
	{
		public static InvalidBean2 valueOf(Long value)
		{
			return null;
		}
	}
}
