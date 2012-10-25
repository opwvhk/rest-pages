package net.sf.opk.beans;

import com.fasterxml.classmate.ResolvedType;


/**
 * A Java Bean property. Instances are only tied to a bean property, not the bean.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public interface BeanProperty
{
	/**
	 * Get the value of the property with its fully resolved type, including generics if available.
	 *
	 * @param javaBean the bean to find the property on
	 * @return the typed value
	 */
	<T> TypedValue<T> getTypedValue(Object javaBean);

	/**
	 * Get the fully resolved type of the property from a bean.
	 *
	 * @param javaBean the bean to resolve the property type on
	 * @return the fully resolved type of the property
	 */
	ResolvedType getType(Object javaBean);

	/**
	 * Get the property value from a bean.
	 *
	 * @param javaBean the bean to find the property on
	 * @return the property value
	 */
	<T> T getValue(Object javaBean);

	/**
	 * Set the property value on a bean.
	 *
	 * @param javaBean the bean to find the property on
	 * @param value    the new property value
	 */
	void setValue(Object javaBean, Object value);

	/**
	 * A typed value to pass along a value (even {@code null}) along with its non-erased type.
	 */
	public static class TypedValue<T>
	{
		private final ResolvedType type;
		private final T value;


		public TypedValue(ResolvedType type, T value)
		{
			this.type = type;
			this.value = value;
		}


		public ResolvedType getType()
		{
			return type;
		}


		public T getValue()
		{
			return value;
		}
	}
}
