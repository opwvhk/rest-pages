package net.sf.opk.beans;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class BeanPropertyExceptionTest
{
	@Test
	public void testConstruction1a()
	{
		String message = "foobar";

		BeanPropertyException exception = new BeanPropertyException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}


	@Test
	public void testConstruction1b()
	{
		BeanPropertyException exception = new BeanPropertyException("foo%s", false);
		assertEquals("foofalse", exception.getMessage());
		assertNull(exception.getCause());
	}


	@Test
	public void testConstruction2a()
	{
		String message = "foobar";
		Throwable cause = new RuntimeException();

		BeanPropertyException exception = new BeanPropertyException(cause, message);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}


	@Test
	public void testConstruction2b()
	{
		Throwable cause = new RuntimeException();

		BeanPropertyException exception = new BeanPropertyException(cause, "foo%s", false);
		assertEquals("foofalse", exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
}
