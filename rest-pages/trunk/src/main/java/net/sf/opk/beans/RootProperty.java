package net.sf.opk.beans;

import com.fasterxml.classmate.ResolvedType;

import static net.sf.opk.rest.util.GenericsUtil.resolveType;


/**
 * Class to expose a Java Bean as a 'property' via the {@code BeanProperty} interface.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class RootProperty implements BeanProperty
{
	@Override
	public <T> TypedValue<T> getTypedValue(Object javaBean)
	{
		ResolvedType type = getType(javaBean);
		T value = (T)getValue(javaBean);
		return new TypedValue<>(type, value);
	}


	@Override
	public ResolvedType getType(Object javaBean)
	{
		return resolveType(javaBean.getClass());
	}


	@Override
	public <T> T getValue(Object javaBean)
	{
		return (T)javaBean;
	}


	@Override
	public void setValue(Object javaBean, Object value)
	{
		throw new BeanPropertyException("Cannot change the root property. Please ensure there are only non-empty " +
		                                "property paths after the prefix.");
	}
}
