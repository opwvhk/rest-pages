package net.sf.opk.rest.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;


/**
 * Utility class for Java Beans.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public final class BeanUtil
{
	/**
	 * Cache for bean properties.
	 */
	private static final Cache<Class<?>, Map<String, PropertyDescriptor>> PROPERTY_CACHE = new Cache<>();


	/**
	 * Find all properties for a bean class.
	 *
	 * @param beanClass the bean class to introspect
	 * @return the properties of the class, mapped by name
	 */
	public static Map<String, PropertyDescriptor> findProperties(Class<?> beanClass)
	{
		Map<String, PropertyDescriptor> result = PROPERTY_CACHE.get(beanClass);
		if (result == null)
		{
			result = new HashMap<>();
			BeanInfo beanInfo = findBeanInfo(beanClass, Object.class);
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors())
			{
				result.put(descriptor.getName(), descriptor);
			}
			PROPERTY_CACHE.put(beanClass, result);
		}
		return result;
	}


	static BeanInfo findBeanInfo(Class<?> beanClass, Class<?> stopClass)
	{
		try
		{
			return Introspector.getBeanInfo(beanClass, stopClass);
		}
		catch (IntrospectionException e)
		{
			throw new IllegalArgumentException(String.format("Failed to get the bean info of %s up to %s.", beanClass,
			                                                 stopClass), e);
		}
	}


	/**
	 * Find a specific property of a bean class.
	 *
	 * @param beanClass the bean class to introspect
	 * @param name      the name of the property to find
	 * @return the property, or null if it does not exist
	 */
	public static PropertyDescriptor findProperty(Class<?> beanClass, String name)
	{
		PropertyDescriptor descriptor = findProperties(beanClass).get(name);
		if (descriptor == null)
		{
			throw new IllegalArgumentException(format("%s has not property named '%s'", beanClass, name));
		}
		return descriptor;
	}


	public static <R> R invoke(Object object, Method method, Object... parameters)
	{
		try
		{
			return (R)method.invoke(object, parameters);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException(format("%s is not a public method", method));
		}
		catch (InvocationTargetException e)
		{
			throw new IllegalArgumentException(format("%s threw an exception", method));
		}
	}


	public static <R> R instantiate(Constructor<R> constructor, Object... parameters)
	{
		try
		{
			return constructor.newInstance(parameters);
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException(format("Cannot create instances of %s", constructor.getDeclaringClass()),
			                                   e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException(format("Not allowed to call %s - is it public?", constructor), e);
		}
		catch (InvocationTargetException e)
		{
			throw new IllegalArgumentException(format("%s threw an exception", constructor), e.getTargetException());
		}
	}


	/**
	 * Utility class: do not instantiate.
	 */
	private BeanUtil()
	{
		System.out.println("");
		// Nothing to do.
	}
}
