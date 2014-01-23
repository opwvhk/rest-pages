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

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.classmate.ResolvedType;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static net.sf.opk.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ConverterByConstructorTest
{
	private ConverterByConstructor converter;


	@Before
	public void initialize()
	{
		converter = new ConverterByConstructor();
	}


	@Test
	public void testPriority() throws Exception
	{
		assertEquals(Integer.MIN_VALUE + 1, converter.getPriority());
	}


	@Test
	public void testImpossibleConversion1() throws Exception
	{
		assertFalse(converter.canConvertTo(resolveType(List.class)));
	}


	@Test
	public void testImpossibleConversion2() throws Exception
	{
		assertFalse(converter.canConvertTo(resolveType(Object.class)));
	}


	@Test(expected = ConversionException.class)
	public void testImpossibleConversion3() throws Exception
	{
		converter.convertTo(resolveType(Object.class), "");
	}


	@Test
	public void testConversion1() throws Exception
	{
		ResolvedType resolvedType = resolveType(BigInteger.class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertEquals(BigInteger.valueOf(123L), converter.convertTo(resolvedType, asList("123", "456")));
	}
}
