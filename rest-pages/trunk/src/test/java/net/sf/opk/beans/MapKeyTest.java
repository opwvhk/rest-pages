package net.sf.opk.beans;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.sf.opk.rest.forms.conversion.ConversionService;

import static java.util.Collections.singletonList;
import static net.sf.opk.rest.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MapKeyTest extends NestedPropertyTestBase
{
	private Map<Integer, String> map;
	private MapKey mapKey;
	private int keyValue;


	@Before
	public void initialize()
	{
		String key = "42";
		keyValue = 42;

		ConversionService conversionService = mock(ConversionService.class);
		when(conversionService.convert(singletonList(key), resolveType(Integer.class))).thenReturn(keyValue);

		map = new HashMap<>();
		map.put(24, "twentyfour");
		map.put(keyValue, "fortytwo");

		mapKey = new MapKey(conversionService, createParentBean(Map.class, Integer.class, String.class), key);
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongBeanType1()
	{
		mapKey.getTypedValue("Not a Map");
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongBeanType2()
	{
		mapKey.setValue("Not a Map", null);
	}


	@Test
	public void testGetValue()
	{
		BeanProperty.TypedValue<String> typedValue = mapKey.getTypedValue(map);
		assertEquals(String.class, typedValue.getType().getErasedType());
		assertEquals(String.class, mapKey.getType(map).getErasedType());
		assertEquals(map.get(keyValue), typedValue.getValue());
		assertEquals(map.get(keyValue), mapKey.getValue(map));
	}


	@Test
	public void testSetValue1()
	{
		mapKey.setValue(map, null);
		assertNull(map.get(keyValue));
	}


	@Test
	public void testSetValue2()
	{
		String newValue = "the answer";
		mapKey.setValue(map, newValue);
		assertEquals(newValue, map.get(keyValue));
	}


	@Test(expected = BeanPropertyException.class)
	public void testWrongValueType()
	{
		mapKey.setValue(map, 42L);
	}
}
