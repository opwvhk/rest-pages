package net.sf.opk.beans.conversion;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;


/**
 * A constraint descriptor for all conversions. This allows them to pose as constraints, which in a way they are. The
 * descriptor provides minimal, empty contents.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class ConversionConstraintDescriptor implements ConstraintDescriptor<Annotation>
{
	/**
	 * The single instance of this class. As there is no real data, more is not needed.
	 */
	public static final ConversionConstraintDescriptor INSTANCE = new ConversionConstraintDescriptor();


	private ConversionConstraintDescriptor()
	{
		// No instanceof this class has any real data. Prevent instantiating it.
	}


	@Override
	public Annotation getAnnotation()
	{
		// We shouldn't return null, but the value is meaningless.
		return new Annotation() {
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return this.getClass();
			}
		};
	}


	@Override
	public Set<Class<?>> getGroups()
	{
		Set<Class<?>> groups = new HashSet<>();
		groups.add(Conversion.class);
		return Collections.unmodifiableSet(groups);
	}


	@Override
	public Set<Class<? extends Payload>> getPayload()
	{
		return Collections.emptySet();
	}


	@Override
	public List<Class<? extends ConstraintValidator<Annotation, ?>>> getConstraintValidatorClasses()
	{
		// Conversions are not validations.
		return Collections.emptyList();
	}


	@Override
	public Map<String, Object> getAttributes()
	{
		return Collections.emptyMap();
	}


	@Override
	public Set<ConstraintDescriptor<?>> getComposingConstraints()
	{
		// Conversions are atomic; they cannot be composed.
		return Collections.emptySet();
	}


	@Override
	public boolean isReportAsSingleViolation()
	{
		return false;
	}
}
