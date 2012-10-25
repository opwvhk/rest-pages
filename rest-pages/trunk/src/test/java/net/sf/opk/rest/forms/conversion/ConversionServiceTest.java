package net.sf.opk.rest.forms.conversion;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static net.sf.opk.rest.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ConversionServiceTest
{
	private static final String TEXT = "abc";
	private static final Long VALUE = 42L;
	private ConversionService conversionService;


	@Before
	public void initialize()
	{
		Converter converter = mock(Converter.class);
		when(converter.canConvertTo(resolveType(Integer.class))).thenReturn(false);
		when(converter.canConvertTo(resolveType(String.class))).thenReturn(true);
		when(converter.convertTo(resolveType(String.class), asList(TEXT))).thenReturn(TEXT);
		when(converter.canConvertTo(resolveType(Long.class))).thenReturn(true);
		when(converter.convertTo(resolveType(Long.class), asList(VALUE.toString()))).thenReturn(VALUE);

		conversionService = new ConversionService(new ConvertersInstance(converter));
	}


	@Test(expected = ConversionException.class)
	public void testConversionFailure()
	{
		conversionService.convert(null, resolveType(Integer.class));
	}


	@Test
	public void testConversionSuccess()
	{
		assertEquals(TEXT, conversionService.convert(asList(TEXT), resolveType(String.class)));
		assertEquals(VALUE, conversionService.convert(asList(VALUE.toString()), resolveType(Long.class)));
	}


	@Test
	public void testConverterCache()
	{
		long time1 = System.nanoTime();
		String result1 = conversionService.convert(asList(TEXT), resolveType(String.class));
		long time2 = System.nanoTime();
		String result2 = conversionService.convert(asList(TEXT), resolveType(String.class));
		long time3 = System.nanoTime();

		assertEquals(TEXT, result1);
		assertEquals(TEXT, result2);
		long beforeCache = time2 - time1;
		long afterCache = time3 - time2;
		assertTrue(afterCache < beforeCache);
	}


	private class ConvertersInstance implements Instance<Converter>
	{
		private List<Converter> converters;


		private ConvertersInstance(Converter converter)
		{
			converters = new ArrayList<>();
			converters.add(converter);
		}


		@Override
		public Instance<Converter> select(Annotation... annotations)
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public <U extends Converter> Instance<U> select(Class<U> uClass, Annotation... annotations)
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public <U extends Converter> Instance<U> select(TypeLiteral<U> uTypeLiteral, Annotation... annotations)
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public boolean isUnsatisfied()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public boolean isAmbiguous()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public Iterator<Converter> iterator()
		{
			return converters.iterator();
		}


		@Override
		public Converter get()
		{
			throw new UnsupportedOperationException();
		}
	}
}
