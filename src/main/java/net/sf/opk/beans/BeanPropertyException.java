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

/**
 * Exception that occurs in {@link BeanProperty} implementations.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class BeanPropertyException extends RuntimeException
{
	/**
	 * Create a {@code BeanPropertyException} with the specified message.
	 *
	 * @param message    a message describing what went wrong
	 * @param parameters parameters for the message (used with {@link String#format(String, Object...)})
	 */
	public BeanPropertyException(String message, Object... parameters)
	{
		super(String.format(message, parameters));
	}


	/**
	 * Create a {@code BeanPropertyException} with the specified message and underlying cause.
	 *
	 * @param cause      the cause for this exception
	 * @param message    a message describing what went wrong
	 * @param parameters parameters for the message (used with {@link String#format(String, Object...)})
	 */
	public BeanPropertyException(Throwable cause, String message, Object... parameters)
	{
		super(String.format(message, parameters), cause);
	}
}
