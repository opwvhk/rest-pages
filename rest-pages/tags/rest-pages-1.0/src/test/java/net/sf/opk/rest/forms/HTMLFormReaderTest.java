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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.junit.Before;
import org.junit.Test;

import net.sf.opk.beans.PropertyParser;
import net.sf.opk.beans.conversion.ConversionService;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


public class HTMLFormReaderTest
{
	private HTMLFormReader messageBodyReader;
	private static final String URLENCODED_FORMDATA = "skipped&field1=abc&currency1=$&currency2=%E2%82%AC&field1=def";


	@Before
	public void initialize()
	{
		PropertyParser propertyParser = mock(PropertyParser.class);
		ConversionService conversionService = mock(ConversionService.class);

		messageBodyReader = new HTMLFormReader(propertyParser, conversionService);

		UriInfo uriInfo = new UriInfoForQueryParameters();
		messageBodyReader.setUriInfo(uriInfo);

		MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(false);
		queryParameters.add("field1", "ghi");
		queryParameters.add("field2", "hello world!");
	}


	@Test
	public void testProxyableClass() throws NoSuchMethodException
	{
		// A proxyable class has a no-arg constructor that does not fail.
		new HTMLFormReader();

		int modifiers = HTMLFormReader.class.getDeclaredConstructor().getModifiers();
		assertTrue(Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers));
	}


	@Test
	public void testAnnotations()
	{
		assertTrue(HTMLFormReader.class.isAnnotationPresent(Provider.class));
		Consumes consumes = HTMLFormReader.class.getAnnotation(Consumes.class);
		assertNotNull(consumes);
		assertEquals(2, consumes.value().length);
		assertEquals(APPLICATION_FORM_URLENCODED, consumes.value()[0]);
		assertEquals(MULTIPART_FORM_DATA, consumes.value()[1]);
	}


	@Test
	public void testIsReadable()
	{
		Class<? extends HTMLForm> subclass = mock(HTMLForm.class).getClass();
		Annotation[] none = new Annotation[0];

		// Actual type
		assertTrue(messageBodyReader.isReadable(HTMLForm.class, HTMLForm.class, none,
		                                        APPLICATION_FORM_URLENCODED_TYPE));
		assertTrue(messageBodyReader.isReadable(HTMLForm.class, HTMLForm.class, none, MULTIPART_FORM_DATA_TYPE));
		// Superclass
		assertTrue(messageBodyReader.isReadable(Object.class, HTMLForm.class, none, APPLICATION_FORM_URLENCODED_TYPE));
		assertTrue(messageBodyReader.isReadable(Object.class, HTMLForm.class, none, MULTIPART_FORM_DATA_TYPE));
		// Incompatible types (subclass and unrelated)
		assertFalse(messageBodyReader.isReadable(subclass, subclass, none, APPLICATION_FORM_URLENCODED_TYPE));
		assertFalse(messageBodyReader.isReadable(subclass, subclass, none, MULTIPART_FORM_DATA_TYPE));
		assertFalse(messageBodyReader.isReadable(Integer.class, Integer.class, none, APPLICATION_FORM_URLENCODED_TYPE));
		assertFalse(messageBodyReader.isReadable(Integer.class, Integer.class, none, MULTIPART_FORM_DATA_TYPE));
		assertFalse(messageBodyReader.isReadable(String.class, String.class, none, APPLICATION_FORM_URLENCODED_TYPE));
		assertFalse(messageBodyReader.isReadable(String.class, String.class, none, MULTIPART_FORM_DATA_TYPE));
	}


	@Test(expected = IllegalStateException.class)
	public void testAnnotationParameterDefault()
	{
		messageBodyReader.getAnnotationParameterDefault(FormCharset.class, "doesNotExist");
	}


	@Test
	public void testUrlEncodedFormWithoutCharset() throws IOException
	{
		ByteArrayInputStream inputStream = new ByteArrayInputStream(URLENCODED_FORMDATA.getBytes("US-ASCII"));
		HTMLForm htmlForm = readHtmlForm(null, inputStream);

		Set<Map.Entry<String, String>> formData = extractFormData(htmlForm);
		verifyFormData(formData, "\u20ac", false);
	}


	private HTMLForm readHtmlForm(String multipartBoundary, InputStream inputStream, Annotation... annotations)
			throws IOException
	{
		MediaType mediaType = MediaType.APPLICATION_FORM_URLENCODED_TYPE;
		if (multipartBoundary != null)
		{
			mediaType = MediaType.valueOf(MULTIPART_FORM_DATA + "; boundary=" + multipartBoundary);
			//mediaType.getParameters().put("boundary", multipartBoundary);
		}
		return messageBodyReader.readFrom(HTMLForm.class, HTMLForm.class, annotations, mediaType, mock(
				MultivaluedMap.class), inputStream);
	}


	private Set<Map.Entry<String, String>> extractFormData(HTMLForm htmlForm)
	{
		Set<Map.Entry<String, String>> formData = new HashSet<>();
		for (Map.Entry<String, String> entry : htmlForm.values())
		{
			formData.add(entry);
		}
		return formData;
	}


	private void verifyFormData(Set<Map.Entry<String, String>> formData, String currency2, boolean withCurrency3)
	{
		assertEquals(withCurrency3 ? 7 : 6, formData.size());
		assertTrue("currency1==$", formData.contains(new AbstractMap.SimpleEntry<>("currency1", "$")));
		assertTrue("currency2==" + currency2, formData.contains(new AbstractMap.SimpleEntry<>("currency2", currency2)));
		if (withCurrency3)
		{
			assertTrue("currency3==\u20ac", formData.contains(new AbstractMap.SimpleEntry<>("currency3", "\u20ac")));
		}
		assertTrue("field1 contains 'abc'", formData.contains(new AbstractMap.SimpleEntry<>("field1", "abc")));
		assertTrue("field1 contains 'def'", formData.contains(new AbstractMap.SimpleEntry<>("field1", "def")));
		assertTrue("field1 contains 'ghi'", formData.contains(new AbstractMap.SimpleEntry<>("field1", "ghi")));
		assertTrue("field2=='hello world!", formData.contains(new AbstractMap.SimpleEntry<>("field2", "hello world!")));
	}


	@Test
	public void testUrlEncodedFormWithCharset1() throws IOException
	{
		ByteArrayInputStream inputStream = new ByteArrayInputStream(URLENCODED_FORMDATA.getBytes("US-ASCII"));
		HTMLForm htmlForm = readHtmlForm(null, inputStream, new RuntimeFormCharset("ISO-8859-1"));

		Set<Map.Entry<String, String>> formData = extractFormData(htmlForm);
		verifyFormData(formData, "\u00e2\u0082\u00ac", false);
	}


	@Test
	public void testUrlEncodedFormWithCharset2() throws IOException
	{
		ByteArrayInputStream inputStream = new ByteArrayInputStream(URLENCODED_FORMDATA.getBytes("US-ASCII"));
		HTMLForm htmlForm = readHtmlForm(null, inputStream, new RuntimeFormCharset("US-ASCII"));

		Set<Map.Entry<String, String>> formData = extractFormData(htmlForm);
		verifyFormData(formData, "\ufffd\ufffd\ufffd", false);
	}


	@Test
	public void testMultipartFormDataWithoutCharset() throws IOException
	{
		InputStream inputStream = getClass().getResourceAsStream("MultipartFormData.txt");
		HTMLForm htmlForm = readHtmlForm("bbb", inputStream);

		Set<Map.Entry<String, String>> formData = extractFormData(htmlForm);
		verifyFormData(formData, "\u20ac", true);
	}


	@Test
	public void testMultipartFormDataWithCharset() throws IOException
	{
		InputStream inputStream = getClass().getResourceAsStream("MultipartFormData.txt");
		HTMLForm htmlForm = readHtmlForm("bbb", inputStream, new RandomAnnotation(), new RuntimeFormCharset(
				"US-ASCII"));

		Set<Map.Entry<String, String>> formData = extractFormData(htmlForm);
		verifyFormData(formData, "\ufffd\ufffd\ufffd", true);
	}


	@Test(expected = WebApplicationException.class)
	public void testMultipartFormDataFailure() throws IOException
	{
		InputStream inputStream = getClass().getResourceAsStream("MultipartFormDataWithError.txt");
		readHtmlForm("bbb", inputStream);
	}


	public static class RuntimeFormCharset extends AnnotationLiteral<FormCharset> implements FormCharset
	{
		private String charset;


		public RuntimeFormCharset(String charset)
		{
			this.charset = charset;
		}


		@Override
		public String value()
		{
			return charset;
		}
	}

	public static class RandomAnnotation extends AnnotationLiteral<Default> implements Default
	{
		// Nothing to implement.
	}

	private static class UriInfoForQueryParameters implements UriInfo
	{
		MultivaluedMap<String, String> encodedQueryParameters = new MultiMap<>();


		@Override
		public String getPath()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public String getPath(boolean b)
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public List<PathSegment> getPathSegments()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public List<PathSegment> getPathSegments(boolean b)
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public URI getRequestUri()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public UriBuilder getRequestUriBuilder()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public URI getAbsolutePath()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public UriBuilder getAbsolutePathBuilder()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public URI getBaseUri()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public UriBuilder getBaseUriBuilder()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public MultivaluedMap<String, String> getPathParameters()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public MultivaluedMap<String, String> getPathParameters(boolean b)
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public MultivaluedMap<String, String> getQueryParameters()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public MultivaluedMap<String, String> getQueryParameters(boolean decode)
		{
			assertFalse(decode);
			return encodedQueryParameters;
		}


		@Override
		public List<String> getMatchedURIs()
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public List<String> getMatchedURIs(boolean b)
		{
			throw new UnsupportedOperationException();
		}


		@Override
		public List<Object> getMatchedResources()
		{
			throw new UnsupportedOperationException();
		}
	}
}
