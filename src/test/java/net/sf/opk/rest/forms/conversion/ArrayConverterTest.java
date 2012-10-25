package net.sf.opk.rest.forms.conversion;

import java.util.List;

import com.fasterxml.classmate.ResolvedType;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static net.sf.opk.rest.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ArrayConverterTest
{
	private static final String VALUE1 = "abc";
	private static final String VALUE2 = "def";
	private static final List<String> VALUES_LIST = asList(VALUE1, VALUE2);
	private static final String[] VALUES_ARRAY = {VALUE1, VALUE2};
	private ArrayConverter converter;


	@Before
	public void initialize()
	{
		ConversionService conversionService = mock(ConversionService.class);
		when(conversionService.convert(asList(VALUE1), resolveType(String.class))).thenReturn(VALUE1);
		when(conversionService.convert(asList(VALUE2), resolveType(String.class))).thenReturn(VALUE2);

		converter = new ArrayConverter(conversionService);
	}


	@Test
	public void testPriority() throws Exception
	{
		assertEquals(Integer.MIN_VALUE + 2, converter.getPriority());
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
	public void testConversion() throws Exception
	{
		ResolvedType resolvedType = resolveType(String[].class);
		assertTrue(converter.canConvertTo(resolvedType));
		assertArrayEquals(VALUES_ARRAY, (Object[])converter.convertTo(resolvedType, VALUES_LIST));
	}
}
