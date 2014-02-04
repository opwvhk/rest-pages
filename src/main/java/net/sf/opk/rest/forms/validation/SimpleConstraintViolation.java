package net.sf.opk.rest.forms.validation;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import net.sf.opk.beans.BeanProperty;


/**
 * A simple constraint violation. Supports additional convenience constructors for this library.
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


	/**
	 * Create a constraint violation, providing all properties.
	 *
	 * @param message              the message describing the violation
	 * @param messageTemplate      the template for the messsage
	 * @param rootBean             the bean on which the violation was reported
	 * @param rootBeanClass        the root bean type
	 * @param leafBean             the property on which the class constraint violation was fuond, or whose property constraint failed
	 * @param propertyPath         the path of the root bean to the invalid property
	 * @param invalidValue         the invalid property value (this property is also set for a conversion constraint, whereas the bean property is not)
	 * @param constraintDescriptor a description of the constraint violation
	 */
	public SimpleConstraintViolation(String message, String messageTemplate, T rootBean, Class<T> rootBeanClass, Object leafBean, Path propertyPath,
	                                 Object invalidValue, ConstraintDescriptor<?> constraintDescriptor)
	{
		this.message = message;
		this.messageTemplate = messageTemplate;
		this.rootBean = rootBean;
		this.rootBeanClass = rootBeanClass;
		this.leafBean = leafBean;
		this.propertyPath = propertyPath;
		this.invalidValue = invalidValue;
		this.constraintDescriptor = constraintDescriptor;
	}


	/**
	 * Create a constraint violation by prefixing the property path of an existing constraint violation.
	 */
	public SimpleConstraintViolation(ConstraintViolation<T> constraintViolation, BeanProperty propertyPrefix)
	{
		this(constraintViolation.getMessage(), constraintViolation.getMessageTemplate(),
		     constraintViolation.getRootBean(), constraintViolation.getRootBeanClass(), constraintViolation.getLeafBean(),
		     propertyPrefix.prefixTo(constraintViolation.getPropertyPath()), constraintViolation.getInvalidValue(),
		     constraintViolation.getConstraintDescriptor());
	}


	/**
	 * Create a constraint violation based on a bean, property, invalid value and message template.
	 *
	 * @param rootBean        the bean on which the violation was reported
	 * @param property        the invalid property
	 * @param invalidValue    the invalid property value (this property is also set for a conversion constraint, whereas the bean property is not)
	 * @param messageTemplate the template for the messsage
	 * @param interpolator    the message interpolator that can determine the actual message to use
	 */
	public SimpleConstraintViolation(T rootBean, BeanProperty property, final Object invalidValue, String messageTemplate, MessageInterpolator interpolator)
	{
		this.rootBean = rootBean;
		rootBeanClass = (Class<T>)rootBean.getClass();
		leafBean = property.getTypedParentValue(rootBean).getValue();
		this.messageTemplate = messageTemplate;
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
