package net.sf.opk.beans;

import com.fasterxml.classmate.ResolvedType;

import static net.sf.opk.rest.util.GenericsUtil.resolveType;


public class NestedPropertyTestBase
{
	protected BeanProperty createParentBean(Class<?> erasedType, Class<?>... typeParameters)
	{
		return new DummyBeanProperty(resolveType(erasedType, typeParameters));
	}


	private class DummyBeanProperty implements BeanProperty
	{
		ResolvedType type;


		private DummyBeanProperty(ResolvedType type)
		{
			this.type = type;
		}


		@Override
		public <T> TypedValue<T> getTypedValue(Object javaBean)
		{
			T value = getValue(javaBean);
			return new TypedValue<>(getType(javaBean), value);
		}


		@Override
		public ResolvedType getType(Object javaBean)
		{
			Class<?> beanClass = javaBean.getClass();
			return type.getErasedType().isAssignableFrom(beanClass) ? type : resolveType(beanClass);
		}


		@Override
		public <T> T getValue(Object javaBean)
		{
			return (T)javaBean;
		}


		@Override
		public void setValue(Object javaBean, Object value)
		{
			throw new BeanPropertyException("Error in test.");
		}
	}
}
