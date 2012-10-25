package net.sf.opk.rest.forms.conversion;

import java.lang.reflect.Constructor;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.rest.util.BeanUtil;
import net.sf.opk.rest.util.Cache;
import net.sf.opk.rest.util.Prioritized;

import static java.lang.String.format;


/**
 * Converter that can convert to any class with a constructor that takes a single String.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class ConverterByConstructor extends SingleValueConverter implements Prioritized
{
	private Cache<Class<?>, Constructor<?>> constructorCache = new Cache<>();


	@Override
	public boolean canConvertTo(ResolvedType resolvedType)
	{
		return getConstructor(resolvedType.getErasedType()) != null;
	}


	private <T> Constructor<T> getConstructor(Class<T> clazz)
	{
		Constructor<T> constructor = (Constructor<T>)constructorCache.get(clazz);
		if (constructor == null)
		{
			constructor = findSingleStringConstructor(clazz);
			constructorCache.put(clazz, constructor);
		}
		return constructor;
	}


	private <T> Constructor<T> findSingleStringConstructor(Class<T> clazz)
	{
		for (Constructor<T> constructor : (Constructor<T>[])clazz.getConstructors())
		{
			if (isSingleStringParameter(constructor.getParameterTypes()))
			{
				return constructor;
			}
		}
		return null;
	}


	@Override
	public <T> T convertTo(ResolvedType resolvedTypez, String value)
	{
        Class<T> clazz = (Class<T>) resolvedTypez.getErasedType();
        Constructor<T> constructor = getConstructor(clazz);
		if (constructor == null)
		{
			throw new ConversionException(format(
					"Cannot convert to %s: the class has no constructor that takes a single String.", clazz));
		}
		return BeanUtil.instantiate(constructor, value);
	}


	@Override
	public int getPriority()
	{
		return Integer.MIN_VALUE + 1;
	}
}
