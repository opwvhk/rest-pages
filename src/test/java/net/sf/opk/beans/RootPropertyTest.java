package net.sf.opk.beans;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class RootPropertyTest
{
	RootProperty rootProperty;


	@Before
	public void initialize()
	{
		rootProperty = new RootProperty();
	}


	@Test
	public void testTypedValue()
	{
		Object bean = 42L;

		BeanProperty.TypedValue<Object> typedValue = rootProperty.getTypedValue(bean);
		assertEquals(Long.class, typedValue.getType().getErasedType());
		assertEquals(bean, typedValue.getValue());
		assertEquals(typedValue.getType(), rootProperty.getType(bean));
		assertEquals(typedValue.getValue(), rootProperty.getValue(bean));
	}


	@Test(expected = BeanPropertyException.class)
	public void testSetValue()
	{
		rootProperty.setValue(new Object(), null);
	}
}
