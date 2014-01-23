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
package net.sf.opk.beans.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;


public class BeanUtilTest extends UtilityClassTestBase
{
	@Test
	public void testUtilityClass() throws InvocationTargetException, IllegalAccessException, InstantiationException
	{
		confirmUtilityClass(BeanUtil.class);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testIntrospectionFailure()
	{
		BeanUtil.findBeanInfo(String.class, Number.class);
	}


	@Test
	public void testFindProperties()
	{
		Map<String, PropertyDescriptor> properties = BeanUtil.findProperties(DummyBean.class);
		assertEquals(2, properties.size());
		assertEquals(String.class, properties.get("name").getPropertyType());
		assertEquals(Integer.TYPE, properties.get("age").getPropertyType());

		Map<String, PropertyDescriptor> properties2 = BeanUtil.findProperties(DummyBean.class);
		assertSame(properties, properties2);
	}


	@Test
	public void testFindProperty1()
	{
		PropertyDescriptor property = BeanUtil.findProperty(DummyBean.class, "name");
		assertEquals(String.class, property.getPropertyType());
	}


	@Test(expected = IllegalArgumentException.class)
	public void testFindProperty2()
	{
		BeanUtil.findProperty(DummyBean.class, "description");
	}


	@Test(expected = IllegalArgumentException.class)
	public void testIllegalMethodInvocation() throws NoSuchMethodException
	{
		DummyBean bean = new DummyBean();
		Method method = DummyBean.class.getDeclaredMethod("foo");
		BeanUtil.invoke(bean, method);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testFailedMethodInvocation() throws NoSuchMethodException
	{
		DummyBean bean = new DummyBean();
		Method method = DummyBean.class.getDeclaredMethod("bar");
		BeanUtil.invoke(bean, method);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testIllegalConstructorInvocation1() throws NoSuchMethodException
	{
		Constructor constructor = DummyBean.class.getDeclaredConstructor(int.class);
		BeanUtil.<DummyBean>instantiate(constructor, 123);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testIllegalConstructorInvocation2() throws NoSuchMethodException
	{
		Constructor constructor = InaccessibleBean.class.getDeclaredConstructor();
		BeanUtil.<InaccessibleBean>instantiate(constructor);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testFailedConstructorInvocation() throws NoSuchMethodException
	{
		Constructor constructor = DummyBean.class.getDeclaredConstructor(String.class);
		String value = null;
		BeanUtil.<DummyBean>instantiate(constructor, value);
	}
}
