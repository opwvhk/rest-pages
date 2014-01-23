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
package net.sf.opk.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.classmate.ResolvedType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;


public class GenericsUtilTest extends UtilityClassTestBase
{
	private Method foo;


	@Before
	public void setUp() throws Exception
	{
		foo = getClass().getMethod("foo");
	}


	@Test
	public void testUtilityClass() throws InvocationTargetException, IllegalAccessException, InstantiationException
	{
		confirmUtilityClass(GenericsUtil.class);
	}


	@Test
	public void testResolveType1()
	{
		ResolvedType resolvedType = GenericsUtil.resolveType(List.class);
		assertEquals(List.class, resolvedType.getErasedType());
		assertEquals(0, resolvedType.getTypeParameters().size());
	}


	@Test
	public void testResolveType2()
	{
		ResolvedType resolvedType = GenericsUtil.resolveType(List.class, String.class);
		assertEquals(List.class, resolvedType.getErasedType());
		assertEquals(1, resolvedType.getTypeParameters().size());
		assertEquals(String.class, resolvedType.getTypeParameters().get(0).getErasedType());
	}


	@Test
	public void testResolveType3()
	{
		ResolvedType stringType = GenericsUtil.resolveType(String.class);
		ResolvedType integerType = GenericsUtil.resolveType(Integer.class);

		ResolvedType resolvedType = GenericsUtil.resolveType(Map.class, stringType, integerType);
		assertEquals(Map.class, resolvedType.getErasedType());
		assertEquals(2, resolvedType.getTypeParameters().size());
		assertEquals(stringType, resolvedType.getTypeParameters().get(0));
		assertEquals(integerType, resolvedType.getTypeParameters().get(1));
	}


	@Test
	public void testResolveReturnType1() throws NoSuchMethodException
	{
		ResolvedType resolvedType = GenericsUtil.resolveReturnType(GenericsUtil.resolveType(getClass()), foo);

		assertEquals(List.class, resolvedType.getErasedType());
		assertEquals(1, resolvedType.getTypeParameters().size());
		assertEquals(String.class, resolvedType.getTypeParameters().get(0).getErasedType());

		// And again: check the value was/is cached.

		ResolvedType resolvedType2 = GenericsUtil.resolveReturnType(GenericsUtil.resolveType(getClass()), foo);
		assertSame(resolvedType, resolvedType2);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testResolveReturnType2() throws NoSuchMethodException
	{
		GenericsUtil.resolveReturnType(GenericsUtil.resolveType(String.class), foo);
	}


	@Test
	public void testFindTypeParameter1()
	{
		ResolvedType type = GenericsUtil.resolveType(DummySubClass1.class);
		assertEquals(String.class, GenericsUtil.findTypeParameter(type, Map.class, 0).getErasedType());
		assertEquals(Integer.class, GenericsUtil.findTypeParameter(type, Map.class, 1).getErasedType());
	}


	@Test
	public void testFindTypeParameter2()
	{
		ResolvedType type = GenericsUtil.resolveType(DummyClass1.class);
		assertEquals(String.class, GenericsUtil.findTypeParameter(type, Map.class, 0).getErasedType());
		assertEquals(Number.class, GenericsUtil.findTypeParameter(type, Map.class, 1).getErasedType());
	}


	@Test
	public void testFindTypeParameter3()
	{
		ResolvedType type = GenericsUtil.resolveType(DummyClass1.class);
		assertEquals(Number.class, GenericsUtil.findTypeParameter(type, DummyClass1.class, 0).getErasedType());
	}


	@Test(expected = IllegalArgumentException.class)
	public void testFindTypeParameter4()
	{
		ResolvedType type = GenericsUtil.resolveType(DummySubClass1.class);
		GenericsUtil.findTypeParameter(type, List.class, 0);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testFindTypeParameter5()
	{
		ResolvedType type = GenericsUtil.resolveType(DummyClass2.class);
		GenericsUtil.findTypeParameter(type, Number.class, 0);
	}


	public List<String> foo()
	{
		return null;
	}


	private class DummySubClass1 extends DummyClass1<Integer>
	{
		// Empty class: we're interested in type parameters only.
	}

	private class DummyClass1<V extends Number> extends AbstractMap<String, V>
	{
		@Override
		public Set<Entry<String, V>> entrySet()
		{
			return Collections.emptySet();
		}
	}

	private class DummyClass2 extends Number
	{
		@Override
		public int intValue()
		{
			return 0;
		}


		@Override
		public long longValue()
		{
			return 0;
		}


		@Override
		public float floatValue()
		{
			return 0;
		}


		@Override
		public double doubleValue()
		{
			return 0;
		}
	}
}
