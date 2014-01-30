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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.validation.Validator;
import javax.ws.rs.core.MediaType;

import com.fasterxml.classmate.ResolvedType;
import org.junit.Before;
import org.junit.Test;

import net.sf.opk.beans.BeanProperty;
import net.sf.opk.beans.PropertyParser;
import net.sf.opk.beans.conversion.ConversionService;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static net.sf.opk.beans.util.GenericsUtil.resolveType;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class HTMLFormTest
{
	private PropertyParser propertyParser;
	private ConversionService conversionService;
	private Validator validator;
	private HTMLForm htmlForm;


	@Before
	public void initialize() throws IOException
	{
		propertyParser = mock(PropertyParser.class);
		conversionService = mock(ConversionService.class);
		validator = mock(Validator.class);

		htmlForm = new HTMLForm(propertyParser, conversionService, validator, null);
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
	public void testApplyValuesToWithoutPrefix()
	{
		Object bean = new Object();

		ResolvedType typeString = resolveType(String.class);
		BeanProperty property1 = mock(BeanProperty.class);
		when(property1.getType(bean)).thenReturn(typeString);
		when(propertyParser.parse("field1")).thenReturn(property1);

		Object value1 = new Object();
		when(conversionService.convert(asList("value1", "value2", "value3", "value4"), typeString)).thenReturn(value1);

		ResolvedType typeBoolean = resolveType(Boolean.class);
		BeanProperty property2 = mock(BeanProperty.class);
		when(property2.getType(bean)).thenReturn(typeBoolean);
		when(propertyParser.parse("parent.field2")).thenReturn(property2);

		Object value2 = new Object();
		when(conversionService.convert(singletonList("true"), typeBoolean)).thenReturn(value2);

		htmlForm.applyValuesTo(bean);

		verify(propertyParser).parse("parent.field2");
		verify(propertyParser).parse("field1");
		verify(property1).setValue(bean, value1);
		verify(property2).setValue(bean, value2);
	}


	@Test
	public void testApplyValuesToWithPrefix()
	{
		Object bean = new Object();

		ResolvedType typeBoolean = resolveType(Boolean.class);
		BeanProperty property2 = mock(BeanProperty.class);
		when(property2.getType(bean)).thenReturn(typeBoolean);
		when(propertyParser.parse("field2")).thenReturn(property2);

		Object value2 = new Object();
		when(conversionService.convert(singletonList("true"), typeBoolean)).thenReturn(value2);

		htmlForm.applyValuesTo("parent", bean);

		verify(property2).setValue(bean, value2);
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
		HTMLForm htmlForm2 = new HTMLForm(propertyParser, conversionService, validator, null);
		addFieldValues(htmlForm2);
		addUploadedFiles(htmlForm2);

		HTMLForm htmlForm3 = new HTMLForm(propertyParser, conversionService, validator, null);
		addFieldValues(htmlForm3);

		HTMLForm htmlForm4 = new HTMLForm(propertyParser, conversionService, validator, null);
		addFieldValues(htmlForm4);

		HTMLForm htmlForm5 = new HTMLForm(propertyParser, conversionService, validator, null);

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
}
