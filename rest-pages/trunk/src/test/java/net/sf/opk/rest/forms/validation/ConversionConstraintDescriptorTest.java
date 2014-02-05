package net.sf.opk.rest.forms.validation;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class ConversionConstraintDescriptorTest
{
	private ConversionConstraintDescriptor constraintDescriptor = ConversionConstraintDescriptor.INSTANCE;


	@Test
	public void testName() throws Exception
	{
		assertNotNull(constraintDescriptor.getAnnotation());
		assertNotNull(constraintDescriptor.getAnnotation().annotationType());
		assertTrue(constraintDescriptor.getAttributes().isEmpty());
		assertTrue(constraintDescriptor.getComposingConstraints().isEmpty());
		assertTrue(constraintDescriptor.getConstraintValidatorClasses().isEmpty());
		assertTrue(constraintDescriptor.getPayload().isEmpty());
		assertFalse(constraintDescriptor.isReportAsSingleViolation());

		Class<?> group = Conversion.class;
		Set<? extends Class<?>> groups = Collections.singleton(group);
		assertEquals(groups, constraintDescriptor.getGroups());
	}
}
