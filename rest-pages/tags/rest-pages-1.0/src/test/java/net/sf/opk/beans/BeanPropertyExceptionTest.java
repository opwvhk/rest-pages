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
