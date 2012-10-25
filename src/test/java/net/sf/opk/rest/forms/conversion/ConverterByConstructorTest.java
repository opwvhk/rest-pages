package net.sf.opk.rest.forms.conversion;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.classmate.ResolvedType;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static net.sf.opk.rest.util.GenericsUtil.resolveType;
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
