/*
 * Copyright 2012 Oscar Westra van Holthe - Kind
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.opk.beans.BeanProperty;
import net.sf.opk.beans.PropertyParser;
import net.sf.opk.rest.forms.conversion.ConversionService;

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
	 * Create an HTMLForm.
	 *
	 * @param propertyParser    the {@code PropertyParser} to use for the {@link #applyValuesTo(String, Object)} method
	 * @param conversionService the {@code conversionService} to use for the {@link #applyValuesTo(String, Object)} method
	 */
	public HTMLForm(PropertyParser propertyParser, ConversionService conversionService)
	{
		this.propertyParser = propertyParser;
		this.conversionService = conversionService;
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
	 * Iterate over all scalar name-value pairs in the form.
	 *
	 * @return an {@code Iterable} for all form data
	 */
	protected Iterable<Map.Entry<String, String>> values()
	{
		List<Map.Entry<String, String>> result = new ArrayList<>();
		for (Map.Entry<String, List<String>> entry : formData.entrySet())
		{
			for (String value : entry.getValue())
			{
				result.add(new AbstractMap.SimpleEntry<>(entry.getKey(), value));
			}
		}
		return result;
	}


	/**
	 * Iterate over all uploaded files (with their field name as key) in the form.
	 *
	 * @return an {@code Iterable} for all uploaded files
	 */
	protected Iterable<Map.Entry<String, UploadedFile>> uploads()
	{
		List<Map.Entry<String, UploadedFile>> result = new ArrayList<>();
		for (Map.Entry<String, List<UploadedFile>> entry : fileUploads.entrySet())
		{
			for (UploadedFile value : entry.getValue())
			{
				result.add(new AbstractMap.SimpleEntry<>(entry.getKey(), value));
			}
		}
		return result;
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
	 * Apply a subset of the scalar form data to the specified bean. The prefix is used as the property the bean
	 * represents. All form fields with that prefix are interpreted
	 *
	 * @param prefix the field name prefix
	 * @param bean   the Java Bean to apply the scalar form data to
	 */
	public void applyValuesTo(String prefix, Object bean)
	{
		if (prefix != null)
		{
			prefix += '.';
		}
		int prefixLength = prefix == null ? 0 : prefix.length();

		for (Map.Entry<String, List<String>> formParameter : formData.entrySet())
		{
			String parameterName = formParameter.getKey();
			if (prefix == null || parameterName.startsWith(prefix))
			{
				BeanProperty property = propertyParser.parse(parameterName.substring(prefixLength));
				Object value = conversionService.convert(formParameter.getValue(), property.getType(bean));
				property.setValue(bean, value);
			}
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
}
