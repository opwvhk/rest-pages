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
package net.sf.opk.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Base class for tests that test utility classes.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class UtilityClassTestBase
{
	public void confirmUtilityClass(Class<?> clazz)
			throws InvocationTargetException, IllegalAccessException, InstantiationException
	{
		CheckClassIsFinal(clazz);
		checkAndTestSinglePrivateNoArgConstructor(clazz);
		checkAllMethodsAreStatic(clazz);
	}


	private void CheckClassIsFinal(Class<?> clazz)
	{
		assertTrue(Modifier.isFinal(clazz.getModifiers()));
	}


	private void checkAndTestSinglePrivateNoArgConstructor(Class<?> clazz)
			throws InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		Constructor<?> constructor = constructors[0];
		assertEquals(0, constructor.getParameterTypes().length);
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));

		constructor.setAccessible(true);
		constructor.newInstance(); // Construction must succeed.
	}


	private void checkAllMethodsAreStatic(Class<?> clazz)
	{
		for (Class<?> aClass = clazz; !aClass.equals(Object.class); aClass = aClass.getSuperclass())
		{
			for (Method method : aClass.getDeclaredMethods())
			{
				assertTrue(Modifier.isStatic(method.getModifiers()));
			}
		}
	}
}
