package net.sf.opk.rest.forms.validation;

import java.util.Collections;
import java.util.Iterator;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import net.sf.opk.beans.BeanProperty;
import net.sf.opk.beans.PropertyParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SimpleConstraintViolationTest
{
	@Test
	public void testPathExtension() throws Exception
	{
		ConstraintDescriptor constraintDescriptor = mock(ConstraintDescriptor.class);
		Object invalidValue = new Object();
		Object leafBean = new Object();
		Object rootBean = new Object();
		String message = "a message";
		String messageTemplate = "{template}";

		Path.Node node = mock(Path.Node.class);
		when(node.getName()).thenReturn("baz");
		Path propertyPath = mock(Path.class);
		when(propertyPath.iterator()).thenReturn(Collections.singleton(node).iterator());

		ConstraintViolation<Object> violation = mock(ConstraintViolation.class);

		when(violation.getConstraintDescriptor()).thenReturn(constraintDescriptor);
		when(violation.getInvalidValue()).thenReturn(invalidValue);
		when(violation.getLeafBean()).thenReturn(leafBean);
		when(violation.getRootBean()).thenReturn(rootBean);
		when(violation.getRootBeanClass()).thenReturn(Object.class);

		when(violation.getMessage()).thenReturn(message);
		when(violation.getMessageTemplate()).thenReturn(messageTemplate);

		when(violation.getPropertyPath()).thenReturn(propertyPath);

		BeanProperty prefixProperty = new PropertyParser(null).parse("foo.bar");

		ConstraintViolation<Object> testViolation = new SimpleConstraintViolation<>(violation, prefixProperty);

		assertSame(constraintDescriptor, testViolation.getConstraintDescriptor());
		assertSame(invalidValue, testViolation.getInvalidValue());
		assertSame(leafBean, testViolation.getLeafBean());
		assertSame(rootBean, testViolation.getRootBean());

		assertEquals(rootBean.getClass(), testViolation.getRootBeanClass());
		assertEquals(message, testViolation.getMessage());
		assertEquals(messageTemplate, testViolation.getMessageTemplate());

		Iterator<Path.Node> testPath = testViolation.getPropertyPath().iterator();
		assertEquals("foo", testPath.next().getName());
		assertEquals("bar", testPath.next().getName());
		assertEquals("baz", testPath.next().getName());
		assertFalse(testPath.hasNext());
	}

	@Test
	public void testWithMessageTemplate() throws Exception
	{
		final Object invalidValue = new Object();

		SimpleChildBean leafBean = new SimpleChildBean();
		SimpleBean rootBean = new SimpleBean();
		rootBean.setChild(leafBean);

		final String message = "a message";
		String messageTemplate = "{template}";

		BeanProperty property = new PropertyParser(null).parse("child.name");

		MessageInterpolator messageInterpolator = mock(MessageInterpolator.class);
		when(messageInterpolator.interpolate(eq(messageTemplate), any(MessageInterpolator.Context.class))).thenAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable
			{
				MessageInterpolator.Context context = (MessageInterpolator.Context)invocation.getArguments()[1];
				assertEquals(ConversionConstraintDescriptor.INSTANCE, context.getConstraintDescriptor());
				assertSame(invalidValue, context.getValidatedValue());
				return message;
			}
		});

		ConstraintViolation<SimpleBean> testViolation = new SimpleConstraintViolation<>(rootBean, property, invalidValue, messageTemplate,
		                                                                                messageInterpolator);

		assertEquals(ConversionConstraintDescriptor.INSTANCE, testViolation.getConstraintDescriptor());

		assertSame(invalidValue, testViolation.getInvalidValue());
		assertSame(leafBean, testViolation.getLeafBean());
		assertSame(rootBean, testViolation.getRootBean());

		assertEquals(rootBean.getClass(), testViolation.getRootBeanClass());
		assertEquals(message, testViolation.getMessage());
		assertEquals(messageTemplate, testViolation.getMessageTemplate());

		Iterator<Path.Node> testPath = testViolation.getPropertyPath().iterator();
		assertEquals("child", testPath.next().getName());
		assertEquals("name", testPath.next().getName());
		assertFalse(testPath.hasNext());
	}

	public static class SimpleBean
	{
		private String name;
		private SimpleChildBean child;


		public String getName()
		{
			return name;
		}


		public void setName(String name)
		{
			this.name = name;
		}


		public SimpleChildBean getChild()
		{
			return child;
		}


		public void setChild(SimpleChildBean child)
		{
			this.child = child;
		}
	}

	public static class SimpleChildBean
	{
		private String name;


		public String getName()
		{
			return name;
		}


		public void setName(String name)
		{
			this.name = name;
		}
	}
}
