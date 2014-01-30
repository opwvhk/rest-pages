/*
 * Copyright 2012-2013 Oscar Westra van Holthe - Kind
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package net.sf.opk.rest.forms;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.Validator;

import net.sf.opk.beans.BeanProperty;
import net.sf.opk.beans.PropertyParser;
import net.sf.opk.beans.conversion.ConversionException;
import net.sf.opk.beans.conversion.ConversionService;
import net.sf.opk.beans.conversion.SimpleConstraintViolation;

import static java.util.Arrays.asList;


/**
 * <p>A representation of an HTML form. You can add values as parsed from user input (e.g. from an HTTP POST request),
 * and apply them to Java Beans.</p>
 *
 * <h2>Character set</h2>
 *
 * <p>Note that browsers sending data using the {@code application/x-www-form-urlencoded} content type cannot specify
 * the character set used. Also, browsers using the {@code multipart/form-data} content type can, but usually
 * don't.</p>
 *
 * <p>As an alternative to this, the HTML specification states what character set is to be used. First choice is the
 * form's {@code accept-charset} attribute. If the data doesn't fit that character set, the character set of the page is
 * used. If that is also insufficient, {@code UTF-8} is the final fallback.</p>
 *
 * <p>For this reason, it is best to always specify the character set explicitly. Use the same character set in the
 * page, the form's {@code accept-charset} attribute and in the {@link FormCharset @FormCharset} annotation on the
 * {@code HTMLForm} parameter. Preferably {@code UTF-8}, as it can handle all possible input.</p>
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 * @see <a href="http://www.w3.org/TR/html401/">The HTML specification, version 4.01</a>
 * @see <a href="http://dev.w3.org/html5/spec/single-page.html">The HTML specification, version 5 (as of 2012-10-21,
 *      this is a <strong>draft</strong></a>
 */
public class HTMLForm
{
	/**
	 * The (scalar) form data. This data can be applied to Java Beans.
	 */
	private final SortedMap<String, List<String>> formData = new TreeMap<>();
	/**
	 * The file uploads in the form.
	 */
	private final SortedMap<String, List<UploadedFile>> fileUploads = new TreeMap<>();
	/**
	 * The {@code PropertyParser} that is used to translate the form data names to Java Bean properties.
	 */
	private PropertyParser propertyParser;
	/**
	 * The {@code ConversionService} that is used to convert the form data to Java Bean property values.
	 */
	private ConversionService conversionService;
	/**
	 * The validator to use to validate beans after applying values to them.
	 */
	private Validator validator;
	/**
	 * The message interpolator to use to translate conversion errors.
	 */
	private MessageInterpolator messageInterpolator;


	/**
	 * Create an HTMLForm.
	 *
	 * @param propertyParser    the {@code PropertyParser} to use for the {@link #applyValuesTo(String, Object)} method
	 * @param conversionService the {@code ConversionService} to use for the {@link #applyValuesTo(String, Object)} method
	 * @param validator         the {@code Validator} to use for the {@link #applyValuesTo(String, Object)} method
	 */
	public HTMLForm(PropertyParser propertyParser, ConversionService conversionService, Validator validator,
	                MessageInterpolator messageInterpolator)
	{
		this.propertyParser = propertyParser;
		this.conversionService = conversionService;
		this.validator = validator;
		this.messageInterpolator = messageInterpolator;
	}


	/**
	 * Add scalar data (name-value pairs) to the form.
	 *
	 * @param name   the name of the name-value pairs
	 * @param values the values of the name-value pairs
	 */
	public void add(String name, String... values)
	{
		add(name, asList(values));
	}


	/**
	 * Add scalar data (name-value pairs) to the form.
	 *
	 * @param name   the name of the name-value pairs
	 * @param values the values of the name-value pairs
	 */
	public void add(String name, List<String> values)
	{
		List<String> currentValues = formData.get(name);
		if (currentValues == null)
		{
			currentValues = new ArrayList<>();
			formData.put(name, currentValues);
		}
		currentValues.addAll(values);
	}


	/**
	 * Add uploaded files (with their field name) to the form.
	 *
	 * @param name    the field name of the file uploads
	 * @param uploads the uploaded files
	 */
	public void addUploads(String name, UploadedFile... uploads)
	{
		addUploads(name, asList(uploads));
	}


	/**
	 * Add uploaded files (with their field name) to the form.
	 *
	 * @param name    the field name of the file uploads
	 * @param uploads the uploaded files
	 */
	public void addUploads(String name, List<UploadedFile> uploads)
	{
		List<UploadedFile> currentUploads = fileUploads.get(name);
		if (currentUploads == null)
		{
			currentUploads = new ArrayList<>();
			fileUploads.put(name, currentUploads);
		}
		currentUploads.addAll(uploads);
	}


	/**
	 * <p>Expose all scalar name-value pairs in the form as an Iterable. Every time an {@link Iterator} is obtained from
	 * the result, it uses the then current form values.</p>
	 *
	 * <p>The name-value pairs are returned in lexicographic key order, and multiple values for a key in submission
	 * order.</p>
	 *
	 * @return an {@code Iterable} for all form data
	 */
	protected Iterable<Map.Entry<String, String>> values()
	{
		return new Iterable<Map.Entry<String, String>>()
		{
			@Override
			public Iterator<Map.Entry<String, String>> iterator()
			{
				return new NestedIterator<>(formData.entrySet().iterator());
			}
		};
	}


	/**
	 * Iterate over all uploaded files (with their field name as key) in the form.
	 *
	 * <p>Expose all uploaded files (with their field name as key) in the form as an Iterable. Every time an {@link
	 * Iterator} is obtained from the result, it uses the then current uploads.</p>
	 *
	 * <p>The name-value pairs are returned in lexicographic key order, and multiple values for a key in submission
	 * order.</p>
	 *
	 * @return an {@code Iterable} for all uploaded files
	 */
	protected Iterable<Map.Entry<String, UploadedFile>> uploads()
	{
		return new Iterable<Map.Entry<String, UploadedFile>>()
		{
			@Override
			public Iterator<Map.Entry<String, UploadedFile>> iterator()
			{
				return new NestedIterator<>(fileUploads.entrySet().iterator());
			}
		};
	}


	/**
	 * Apply all scalar form data to the specified bean.
	 *
	 * @param bean the Java Bean to apply all scalar form data to
	 */
	public void applyValuesTo(Object bean)
	{
		applyValuesTo(null, bean);
	}


	/**
	 * <p>Apply a subset of the scalar form data to the specified bean.</p>
	 *
	 * <p>If no prefix is specified, all form fields will be applied to the bean as properties.</p>
	 *
	 * <p>If a prefix is specified, it is assumed to be a property whose value is the supplied bean. All form fields
	 * that represent nested properties of the prefix will be applied to the bean as properties.</p>
	 *
	 * <p>In both cases, each form field value is converted and validated before it is applied, and afterwards the
	 * bean as a whole is also validated after the properties are set.</p>
	 *
	 * @param prefix an optional qualified Java identifier, used as field name prefix
	 * @param bean   the Java Bean to apply the scalar form data to
	 * @return a (hopefully empty) set of constraint violations, using the
	 */
	public Set<ConstraintViolation<?>> applyValuesTo(String prefix, Object bean)
	{
		Path prefixPath = null;
		String nonNullPrefix = "";
		if (prefix != null)
		{
			prefixPath = propertyParser.parse(prefix).toPath();
			nonNullPrefix = prefix + '.';
		}

		Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
		for (Map.Entry<String, List<String>> formParameter : formData.entrySet())
		{
			String parameterName = formParameter.getKey();
			if (parameterName.startsWith(nonNullPrefix))
			{
				String propertyName = parameterName.substring(nonNullPrefix.length());
				List<String> formValue = formParameter.getValue();
				setBeanProperty(bean, propertyName, formValue, constraintViolations);
			}
		}
		return prefixConstraintViolationPaths(constraintViolations, prefixPath);
	}


	private Set<ConstraintViolation<?>> prefixConstraintViolationPaths(Set<ConstraintViolation<?>> constraintViolations,
	                                                                   Path prefixPath)
	{
		Set<ConstraintViolation<?>> prefixedConstraintViolations = new HashSet<>();
		for (ConstraintViolation<?> constraintViolation : constraintViolations)
		{
			Object rootBean = constraintViolation.getRootBean();
			Object invalidValue = constraintViolation.getInvalidValue();

			Path prefixedPath = concatenatePaths(prefixPath, constraintViolation.getPropertyPath());

			prefixedConstraintViolations.add(new SimpleConstraintViolation<Object>(rootBean, null, invalidValue, null,
			                                                                  null));
		}

		return prefixedConstraintViolations;
	}


	private Path concatenatePaths(Path prefixPath, Path propertyPath)
	{
		//PathBui
		return propertyPath;
	}


	/**
	 * Sets a bean property. If successful, the bean is altered. If not, a {@code ConstraintViolation} is added to the
	 * collection of constraint violations.
	 *
	 * @param bean                 the bean whose property to set
	 * @param propertyName         the property to set
	 * @param formValue            the value(s) to set the bean property to
	 * @param constraintViolations the collection to add constraint violations to when they arise
	 */
	private void setBeanProperty(Object bean, String propertyName, List<String> formValue,
	                             Collection<ConstraintViolation<?>> constraintViolations)
	{
		BeanProperty property = propertyParser.parse(propertyName);
		try
		{
			Object value = conversionService.convert(formValue, property.getType(bean));
			property.setValue(bean, value);
			constraintViolations.addAll(validator.validateProperty(bean, propertyName));
		}
		catch (ConversionException e)
		{
			constraintViolations.add(new SimpleConstraintViolation<>(bean, property, formValue, e,
			                                                         messageInterpolator));
		}
	}


	/**
	 * Get all values of a form field.
	 *
	 * @param name the name of the form field
	 * @return all values of the form field, or an empty list if the field does not exist
	 */
	public List<String> getValues(String name)
	{
		List<String> values = formData.get(name);
		if (values == null)
		{
			values = Collections.emptyList();
		}
		else
		{
			values = Collections.unmodifiableList(values);
		}
		return values;
	}


	/**
	 * Get the first value of a form field.
	 *
	 * @param name the name of the form field
	 * @return the first field value, or {@code null} if the field does not exist
	 */
	public String getFirstValue(String name)
	{
		List<String> values = getValues(name);
		return values.isEmpty() ? null : values.get(0);
	}


	@Override
	public String toString()
	{
		return String.format("HTMLForm{formData=%s, fileUploads=%s}", formData, fileUploads);
	}


	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof HTMLForm))
		{
			return false;
		}

		HTMLForm htmlForm = (HTMLForm)o;
		return fileUploads.equals(htmlForm.fileUploads) && formData.equals(htmlForm.formData);
	}


	@Override
	public int hashCode()
	{
		int result = formData.hashCode();
		result = 31 * result + fileUploads.hashCode();
		return result;
	}


	private class NestedIterator<T> implements Iterator<Map.Entry<String, T>>
	{
		/**
		 * Assumption: this iterator has a list with at least one value for each key. If not, the combo hasNext/next doesn't
		 * work correctly.
		 */
		private final Iterator<Map.Entry<String, List<T>>> formValues;
		private String currentKey;
		private Iterator<T> currentValues;


		protected NestedIterator(Iterator<Map.Entry<String, List<T>>> formValues)
		{
			this.formValues = formValues;
			currentKey = null;
			currentValues = null;
		}


		@Override
		public boolean hasNext()
		{
			boolean thereIsANextValue = false;

			if (currentValues != null)
			{
				thereIsANextValue = currentValues.hasNext();
			}

			if (!thereIsANextValue)
			{
				// The values for the current key are exhausted (the current key and values may already be null).
				currentKey = null;
				currentValues = null;
				thereIsANextValue = formValues.hasNext();
			}

			return thereIsANextValue;
		}


		@Override
		public Map.Entry<String, T> next()
		{
			if (currentValues == null)
			{
				Map.Entry<String, List<T>> entry = formValues.next();
				currentKey = entry.getKey();
				currentValues = entry.getValue().iterator();
			}
			return new AbstractMap.SimpleEntry<>(currentKey, currentValues.next());
		}


		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("Removing form values is not supported.");
		}
	}
}
