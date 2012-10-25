package net.sf.opk.beans;

import com.fasterxml.classmate.ResolvedType;


/**
 * A Java Bean property. Instances are only tied to a bean property, not the bean.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public abstract class NestedBeanProperty implements BeanProperty
{
	@Override
	public ResolvedType getType(Object javaBean)
	{
		TypedValue<?> typedValue = getTypedValue(javaBean);
		return typedValue.getType();
	}


	@Override
	public <T> T getValue(Object javaBean)
	{
		TypedValue<T> typedValue = getTypedValue(javaBean);
		return typedValue.getValue();
	}
}
