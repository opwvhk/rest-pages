package net.sf.opk.beans.conversion;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import net.sf.opk.beans.BeanProperty;
import net.sf.opk.beans.NestedBeanProperty;


/**
 * Desciption of a violation of the constraints necessary for conversion of a String to a bean property type.
 *
 * @author <a href="mailto:owestra@bol.com">Oscar Westra van Holthe - Kind</a>
 */
public class SimpleConstraintViolation<T> implements ConstraintViolation<T>
{
	private final String message;
	private final String messageTemplate;
	private final T rootBean;
	private final Class<T> rootBeanClass;
	private final Object leafBean;
	private final Path propertyPath;
	private final Object invalidValue;
	private final ConstraintDescriptor<?> constraintDescriptor;


	public SimpleConstraintViolation(T rootBean, BeanProperty property, final Object invalidValue,
	                                 ConversionException conversionException, MessageInterpolator interpolator)
	{
		// TODO (OPWvH-K, 2014-01-24): Move complexity to HtmlForm
		this.rootBean = rootBean;
		rootBeanClass = (Class<T>)rootBean.getClass();
		if (property instanceof NestedBeanProperty)
		{
			leafBean = ((NestedBeanProperty)property).getTypedParentValue(rootBean).getValue();
		}
		else
		{
			leafBean = rootBean;
		}
		messageTemplate = conversionException.getMessage();
		propertyPath = property.toPath();
		this.invalidValue = invalidValue;
		constraintDescriptor = ConversionConstraintDescriptor.INSTANCE;

		MessageInterpolator.Context context = new MessageInterpolator.Context()
		{
			@Override
			public ConstraintDescriptor<?> getConstraintDescriptor()
			{
				return ConversionConstraintDescriptor.INSTANCE;
			}


			@Override
			public Object getValidatedValue()
			{
				return invalidValue;
			}
		};
		message = interpolator.interpolate(messageTemplate, context);
	}


	@Override
	public String getMessage()
	{
		return message;
	}


	@Override
	public String getMessageTemplate()
	{
		return messageTemplate;
	}


	@Override
	public T getRootBean()
	{
		return rootBean;
	}


	@Override
	public Class<T> getRootBeanClass()
	{
		return rootBeanClass;
	}


	@Override
	public Object getLeafBean()
	{
		return leafBean;
	}


	@Override
	public Path getPropertyPath()
	{
		return propertyPath;
	}


	@Override
	public Object getInvalidValue()
	{
		return invalidValue;
	}


	@Override
	public ConstraintDescriptor<?> getConstraintDescriptor()
	{
		return constraintDescriptor;
	}
}
