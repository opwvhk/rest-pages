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
package net.sf.opk.example;

import java.util.Collections;

import com.fasterxml.classmate.ResolvedType;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static net.sf.opk.rest.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class TrimmingStringConverterTest
{
	private TrimmingStringConverter converter;


	@Before
	public void initialize()
	{
		converter = new TrimmingStringConverter();
	}


	@Test
	public void testImpossibleConversion1() throws Exception
	{
		assertFalse(converter.canConvertTo(resolveType(boolean.class)));
	}


	@Test
	public void testImpossibleConversion2() throws Exception
	{
		assertFalse(converter.canConvertTo(resolveType(TrimmingStringConverterTest.class)));
	}


	@Test
	public void testConversion1() throws Exception
	{
		ResolvedType resolvedType = resolveType(String.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertNull(converter.convertTo(resolvedType, Collections.<String>emptyList()));
	}


	@Test
	public void testConversion2() throws Exception
	{
		ResolvedType resolvedType = resolveType(String.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertNull(converter.convertTo(resolvedType, asList(null, "def")));
	}


	@Test
	public void testConversion3() throws Exception
	{
		ResolvedType resolvedType = resolveType(String.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertNull(converter.convertTo(resolvedType, asList("  ", "def")));
	}


	@Test
	public void testConversion4() throws Exception
	{
		ResolvedType resolvedType = resolveType(String.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertEquals("abc", converter.convertTo(resolvedType, asList("abc", "def")));
	}


	@Test
	public void testConversion5() throws Exception
	{
		ResolvedType resolvedType = resolveType(String.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertEquals("abc", converter.convertTo(resolvedType, asList("  abc\n", "def")));
	}
}
