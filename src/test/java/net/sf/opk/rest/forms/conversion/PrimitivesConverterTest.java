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

import java.util.List;

import com.fasterxml.classmate.ResolvedType;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static net.sf.opk.rest.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PrimitivesConverterTest
{
	private PrimitivesConverter converter;
	private ConverterByValueOf mockConverter;


	@Before
	public void initialize()
	{
		mockConverter = mock(ConverterByValueOf.class);
		converter = new PrimitivesConverter(mockConverter);
	}


	@Test
	public void testPriority() throws Exception
	{
		assertEquals(Integer.MIN_VALUE + 1, converter.getPriority());
	}


	@Test
	public void testForObject()
	{
		assertFalse(converter.canConvertTo(resolveType(Object.class)));
	}


	@Test
	public void testForBoolean()
	{
		testForPrimitive(resolveType(Boolean.TYPE), resolveType(Boolean.class), "true", true);
	}


	public void testForPrimitive(ResolvedType primitiveType, ResolvedType wrapperType, String input, Object converted)
	{
		List<String> values = asList(input);
		when(mockConverter.convertTo(wrapperType, values)).thenReturn(converted);

		assertTrue(converter.canConvertTo(primitiveType));
		assertEquals(converted, converter.convertTo(primitiveType, values));
	}


	@Test
	public void testForByte()
	{
		testForPrimitive(resolveType(Boolean.TYPE), resolveType(Boolean.class), "42", (byte)42);
	}


	@Test
	public void testForShort()
	{
		testForPrimitive(resolveType(Short.TYPE), resolveType(Short.class), "42", (short)42);
	}


	@Test
	public void testForChar()
	{
		testForPrimitive(resolveType(Character.TYPE), resolveType(Character.class), "42", (char)42);
	}


	@Test
	public void testForInt()
	{
		testForPrimitive(resolveType(Integer.TYPE), resolveType(Integer.class), "42", 42);
	}


	@Test
	public void testForLong()
	{
		testForPrimitive(resolveType(Long.TYPE), resolveType(Long.class), "42", 42L);
	}


	@Test
	public void testForFloat()
	{
		testForPrimitive(resolveType(Float.TYPE), resolveType(Float.class), "42", (float)42);
	}


	@Test
	public void testForDouble()
	{
		testForPrimitive(resolveType(Double.TYPE), resolveType(Double.class), "42", (double)42);
	}
}
