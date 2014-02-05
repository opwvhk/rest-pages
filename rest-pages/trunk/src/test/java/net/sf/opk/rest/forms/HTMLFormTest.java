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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Max;
import javax.ws.rs.core.MediaType;

import com.fasterxml.classmate.ResolvedType;
import org.junit.Before;
import org.junit.Test;

import net.sf.opk.beans.ConversionService;
import net.sf.opk.beans.PropertyParser;
import net.sf.opk.beans.converters.ConversionException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class HTMLFormTest
{
	private ConversionService conversionService;
	private HTMLForm htmlForm;


	@Before
	public void initialize() throws IOException
	{
		conversionService = mock(ConversionService.class);

		PropertyParser propertyParser = new PropertyParser(conversionService);
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		Validator validator = validatorFactory.getValidator();
		MessageInterpolator messageInterpolator = validatorFactory.getMessageInterpolator();

		htmlForm = new HTMLForm(propertyParser, conversionService, validator, messageInterpolator);
		addFieldValues(htmlForm);
		addUploadedFiles(htmlForm);
	}


	private void addFieldValues(HTMLForm htmlForm)
	{
		htmlForm.add("field1", "value1");
		htmlForm.add("field1", "value2");
		htmlForm.add("field1", "value3", "value4");
		htmlForm.add("parent.field2", "true");
	}


	private void addUploadedFiles(HTMLForm htmlForm) throws IOException
	{
		UploadedFile textFile1 = new UploadedFile("README.txt", MediaType.TEXT_PLAIN_TYPE, new ByteArrayInputStream(
				"A simple file.".getBytes("US-ASCII")));
		UploadedFile textFile2 = new UploadedFile("INSTALL.txt", MediaType.TEXT_PLAIN_TYPE, new ByteArrayInputStream(
				"Another small file.".getBytes("US-ASCII")));
		htmlForm.addUploads("smallFile", textFile1);
		htmlForm.addUploads("otherFiles", textFile1, textFile2);
		htmlForm.addUploads("otherFiles", textFile2);
	}


	@Test
	public void testFieldValues() throws IOException
	{
		Iterator<Map.Entry<String,String>> values = htmlForm.values().iterator();
		Map.Entry<String, String> entry;

		String field1 = "field1";

		assertTrue(values.hasNext());
		entry = values.next();
		assertEquals(field1, entry.getKey());
		assertEquals("value1", entry.getValue());

		assertTrue(values.hasNext());
		entry = values.next();
		assertEquals(field1, entry.getKey());
		assertEquals("value2", entry.getValue());

		assertTrue(values.hasNext());
		entry = values.next();
		assertEquals(field1, entry.getKey());
		assertEquals("value3", entry.getValue());

		assertTrue(values.hasNext());
		entry = values.next();
		assertEquals(field1, entry.getKey());
		assertEquals("value4", entry.getValue());

		assertEquals("value1", htmlForm.getFirstValue(field1));
		assertEquals(asList("value1", "value2", "value3", "value4"), htmlForm.getValues(field1));

		String field2 = "parent.field2";

		assertTrue(values.hasNext());
		entry = values.next();
		assertEquals(field2, entry.getKey());
		assertEquals("true", entry.getValue());

		assertFalse(values.hasNext());

		assertNull(htmlForm.getFirstValue("nonexistent"));
		assertEquals(Collections.<String>emptyList(), htmlForm.getValues("nonexistent"));
	}


	@Test(expected = UnsupportedOperationException.class)
	public void testFieldValuesRemovalFailure() throws IOException
	{
		Iterator<Map.Entry<String,String>> values = htmlForm.values().iterator();
		values.next();
		values.remove();
	}


	@Test
	public void testUploadedFiles() throws IOException
	{
		Map<String, List<UploadedFile>> formUploads = extractValues(htmlForm.uploads());
		assertEquals(2, formUploads.size());
		List<UploadedFile> uploads1 = formUploads.get("smallFile");
		assertEquals(1, uploads1.size());
		assertTextFile1(uploads1.get(0));
		List<UploadedFile> uploads2 = formUploads.get("otherFiles");
		assertEquals(3, uploads2.size());
		assertTextFile1(uploads2.get(0));
		assertTextFile2(uploads2.get(1));
		assertTextFile2(uploads2.get(2));
	}


	private <V> Map<String, List<V>> extractValues(Iterable<Map.Entry<String, V>> valuesIterable)
	{
		Map<String, List<V>> formUploads = new HashMap<>();
		for (Map.Entry<String, V> entry : valuesIterable)
		{
			String key = entry.getKey();
			List<V> values = formUploads.get(key);
			if (values == null)
			{
				values = new ArrayList<>();
				formUploads.put(key, values);
			}
			values.add(entry.getValue());
		}
		return formUploads;
	}


	private void assertTextFile1(UploadedFile upload) throws IOException
	{
		assertEquals("README.txt", upload.getFileName());
		assertEquals(MediaType.TEXT_PLAIN_TYPE, upload.getMimeType());
		assertEquals(14, upload.getFileSize());
		assertArrayEquals("A simple file.".getBytes("US-ASCII"), upload.getContents());
	}


	private void assertTextFile2(UploadedFile upload) throws IOException
	{
		assertEquals("INSTALL.txt", upload.getFileName());
		assertEquals(MediaType.TEXT_PLAIN_TYPE, upload.getMimeType());
		assertEquals(19, upload.getFileSize());
		assertArrayEquals("Another small file.".getBytes("US-ASCII"), upload.getContents());
	}


	@Test
	public void testSuccessfulApplyValuesToWithoutPrefix()
	{
		SimpleBean bean = new SimpleBean();
		bean.setParent(new SimpleParentBean());

		Object value1 = new Object();
		when(conversionService.convert(eq(asList("value1", "value2", "value3", "value4")), any(ResolvedType.class))).thenReturn(value1);

		when(conversionService.convert(eq(singletonList("true")), any(ResolvedType.class))).thenReturn(true);

		assertTrue(htmlForm.applyValuesTo(bean).isEmpty());

		assertSame(value1, bean.getField1());
		assertTrue(bean.getParent().isField2());
	}


	@Test
	public void testSuccessfulApplyValuesToWithPrefix()
	{
		SimpleParentBean bean = new SimpleParentBean();

		when(conversionService.convert(eq(singletonList("true")), any(ResolvedType.class))).thenReturn(true);

		when(conversionService.convert(eq(singletonList("2")), any(ResolvedType.class))).thenReturn(2);

		htmlForm.add("parent.field3", "2");
		assertTrue(htmlForm.applyValuesTo("parent", bean).isEmpty());

		assertTrue(bean.isField2());
		assertEquals(2, bean.getField3());
	}


	@Test
	public void testFailingApplyValuesToWithPrefix()
	{
		String message = "an error";

		SimpleParentBean bean = new SimpleParentBean();

		when(conversionService.convert(eq(singletonList("true")), any(ResolvedType.class))).thenThrow(new ConversionException(message));

		when(conversionService.convert(eq(singletonList("5")), any(ResolvedType.class))).thenReturn(5);

		htmlForm.add("parent.field3", "5");
		Set<ConstraintViolation<SimpleParentBean>> violations = htmlForm.applyValuesTo("parent", bean);

		assertFalse(bean.isField2()); // Default value: field is not set.

		assertEquals(5, bean.getField3());

		assertEquals(2, violations.size());
		Set<String> messages = new HashSet<>(2);
		Set<String> paths = new HashSet<>(2);
		for (ConstraintViolation<SimpleParentBean> violation : violations)
		{
			messages.add(violation.getMessage());
			paths.add(path(violation.getPropertyPath()));
		}
		assertEquals(new HashSet<>(asList(message, "must be less than or equal to 3")), messages);
		assertEquals(new HashSet<>(asList("parent.field2", "parent.field3")), paths);
	}


	private String path(Path propertyPath)
	{
		StringBuilder buffer = new StringBuilder();

		for (Path.Node node : propertyPath)
		{
			buffer.append('.').append(node.getName());
		}

		return buffer.substring(1);
	}


	@Test
	public void testStringRepresentation()
	{
		String fields = "{field1=[value1, value2, value3, value4], parent.field2=[true]}";
		String file1 = "UploadedFile{fileName='README.txt', mimeType=text/plain, size=14 bytes}";
		String file2 = "UploadedFile{fileName='INSTALL.txt', mimeType=text/plain, size=19 bytes}";
		String uploads = "{otherFiles=[" + file1 + ", " + file2 + ", " + file2 + "], smallFile=[" + file1 + "]}";
		assertEquals("HTMLForm{formData=" + fields + ", fileUploads=" + uploads + "}", htmlForm.toString());
	}


	@Test
	public void testEqualsAndHashCode() throws IOException
	{
		HTMLForm htmlForm2 = new HTMLForm(null, null, null, null);
		addFieldValues(htmlForm2);
		addUploadedFiles(htmlForm2);

		HTMLForm htmlForm3 = new HTMLForm(null, null, null, null);
		addFieldValues(htmlForm3);

		HTMLForm htmlForm4 = new HTMLForm(null, null, null, null);
		addFieldValues(htmlForm4);

		HTMLForm htmlForm5 = new HTMLForm(null, null, null, null);

		assertEquals(htmlForm.hashCode(), htmlForm.hashCode());
		assertFalse(htmlForm.hashCode() == htmlForm2.hashCode());
		assertFalse(htmlForm.hashCode() == htmlForm3.hashCode());
		assertFalse(htmlForm.hashCode() == htmlForm4.hashCode());
		assertFalse(htmlForm.hashCode() == htmlForm5.hashCode());
		assertEquals(htmlForm3.hashCode(), htmlForm4.hashCode());

		assertTrue(htmlForm.equals(htmlForm));
		assertTrue(htmlForm3.equals(htmlForm3));
		assertTrue(htmlForm3.equals(htmlForm4));

		//noinspection ObjectEqualsNull
		assertFalse(htmlForm.equals(null));
		//noinspection EqualsBetweenInconvertibleTypes
		assertFalse(htmlForm.equals(""));
		assertFalse(htmlForm.equals(htmlForm2));
		assertFalse(htmlForm.equals(htmlForm3));
		assertFalse(htmlForm3.equals(htmlForm5));
	}


	public static class SimpleBean
	{
		private SimpleParentBean parent;
		private Object field1;


		public SimpleParentBean getParent()
		{
			return parent;
		}


		public void setParent(SimpleParentBean parent)
		{
			this.parent = parent;
		}


		public Object getField1()
		{
			return field1;
		}


		public void setField1(Object field1)
		{
			this.field1 = field1;
		}
	}

	public static class SimpleParentBean
	{
		private boolean field2;
		@Max(3)
		private int field3;


		public boolean isField2()
		{
			return field2;
		}


		public void setField2(boolean field2)
		{
			this.field2 = field2;
		}


		public int getField3()
		{
			return field3;
		}


		public void setField3(int field3)
		{
			this.field3 = field3;
		}
	}
}
