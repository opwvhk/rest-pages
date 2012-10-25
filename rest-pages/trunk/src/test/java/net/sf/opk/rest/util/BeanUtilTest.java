package net.sf.opk.rest.util;

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
		Map<String,PropertyDescriptor> properties = BeanUtil.findProperties(DummyBean.class);
		assertEquals(2, properties.size());
		assertEquals(String.class, properties.get("name").getPropertyType());
		assertEquals(Integer.TYPE, properties.get("age").getPropertyType());

		Map<String,PropertyDescriptor> properties2 = BeanUtil.findProperties(DummyBean.class);
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
