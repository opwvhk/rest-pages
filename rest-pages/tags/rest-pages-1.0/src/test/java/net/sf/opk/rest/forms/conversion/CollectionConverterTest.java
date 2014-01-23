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
package net.sf.opk.rest.forms.conversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.fasterxml.classmate.ResolvedType;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static net.sf.opk.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CollectionConverterTest
{
	private static final String VALUE1 = "def";
	private static final String VALUE2 = "abc";
	private static final List<String> VALUES_LIST = asList(VALUE1, VALUE2);
	private CollectionConverter converter;


	@Before
	public void initialize()
	{
		ConversionService conversionService = mock(ConversionService.class);
		when(conversionService.convert(asList(VALUE1), resolveType(String.class))).thenReturn(VALUE1);
		when(conversionService.convert(asList(VALUE2), resolveType(String.class))).thenReturn(VALUE2);

		converter = new CollectionConverter(conversionService);
	}


	@Test
	public void testPriority() throws Exception
	{
		assertEquals(Integer.MIN_VALUE + 3, converter.getPriority());
	}


	@Test
	public void testImpossibleConversion1() throws Exception
	{
		assertFalse(converter.canConvertTo(resolveType(String.class)));
	}


	@Test(expected = ConversionException.class)
	public void testImpossibleConversion2() throws Exception
	{
		converter.convertTo(resolveType(String.class), asList(""));
	}


	@Test
	public void testConversion1() throws Exception
	{
		ResolvedType resolvedType = resolveType(List.class, String.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertEquals(VALUES_LIST, converter.convertTo(resolvedType, VALUES_LIST));
	}


	@Test
	public void testConversion2() throws Exception
	{
		ResolvedType resolvedType = resolveType(Set.class, String.class);
		assertTrue(converter.canConvertTo(resolvedType));
		Object result = converter.convertTo(resolvedType, VALUES_LIST);
		assertTrue(result instanceof Set);
		assertEquals(2, ((Set)result).size());
		assertTrue(((Set)result).contains(VALUE1));
		assertTrue(((Set)result).contains(VALUE2));
	}


	@Test
	public void testConversion3() throws Exception
	{
		ResolvedType resolvedType = resolveType(SortedSet.class, String.class);
		assertTrue(converter.canConvertTo(resolvedType));
		Object result = converter.convertTo(resolvedType, VALUES_LIST);
		assertTrue(result instanceof SortedSet);
		List<String> expected = asList(VALUE2, VALUE1);
		List<String> actual = new ArrayList<>((Collection<String>)result);
		assertEquals(expected, actual);
	}
}
