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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.ContentDisposition;
import javax.mail.internet.MimeUtility;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.jvnet.mimepull.MIMEConfig;
import org.jvnet.mimepull.MIMEMessage;
import org.jvnet.mimepull.MIMEPart;

import net.sf.opk.beans.PropertyParser;
import net.sf.opk.rest.forms.conversion.ConversionService;

import static java.lang.String.format;
import static javax.mail.internet.MimeUtility.decodeText;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA_TYPE;


/**
 * A {@code MessageBodyReader} that parses requests originating from HTML forms.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
@ApplicationScoped
@Provider
@Consumes({APPLICATION_FORM_URLENCODED, MULTIPART_FORM_DATA})
public class HTMLFormReader implements MessageBodyReader<HTMLForm>
{
	private static final String MEDIA_TYPE_PARAMETER_MULTIPART_BOUNDARY = "boundary";
	private static final String PART_ENCODING = "content-transfer-encoding";
	private static final String PART_ENCODING_DEFAULT = "7bit";
	private static final String PART_DISPOSITION = "content-disposition";
	private static final String PART_DISPOSITION_PARTNAME = "name";
	private static final String PART_DISPOSITION_FILENAME = "filename";
	private static final String PART_TYPE = "content-type";
	private static final String PART_TYPE_DEFAULT = APPLICATION_OCTET_STREAM;
	private static final String PART_TYPE_CHARSET = "charset";
	private MIMEConfig config = new MIMEConfig();
	private PropertyParser propertyParser;
	private ConversionService conversionService;
	private UriInfo uriInfo;


	/**
	 * No-arg constructor to create proxies with. <strong>This constructor leaves the object in an invalid state!</strong>
	 */
	protected HTMLFormReader()
	{
		// Nothing to do.
	}


	/**
	 * Create a reader for {@code HTMLForm}s. Note that the instance will not work properly without first supplying a
	 * {@code UriInfo} with {@link #setUriInfo(UriInfo)}.
	 *
	 * @param propertyParser    the {@code PropertyParse} to use to parse field names
	 * @param conversionService the {@code ConversionService} to use to convert field values
	 */
	@Inject
	public HTMLFormReader(PropertyParser propertyParser, ConversionService conversionService)
	{
		this.propertyParser = propertyParser;
		this.conversionService = conversionService;
	}


	/**
	 * Setter to inject the required {@code UriInfo} with. Without this, the instance cannot process request parameters in
	 * the URI.
	 *
	 * @param uriInfo the {@code UriInfo} of the request being read
	 */
	@Context
	public void setUriInfo(UriInfo uriInfo)
	{
		this.uriInfo = uriInfo;
	}


	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return type.isAssignableFrom(HTMLForm.class);
	}


	@Override
	public HTMLForm readFrom(Class<HTMLForm> htmlFormClass, Type type, Annotation[] annotations, MediaType mediaType,
	                         MultivaluedMap<String, String> httpHeaders, InputStream inputStream)
			throws IOException, WebApplicationException
	{
		try
		{
			HTMLForm htmlForm = new HTMLForm(propertyParser, conversionService);

			// Determnine the type and character set.

			String charset = determineCharset(annotations);
			boolean isMultipartForm = MULTIPART_FORM_DATA_TYPE.isCompatible(mediaType);

			// Parse the body.

			if (isMultipartForm)
			{
				String boundary = mediaType.getParameters().get(MEDIA_TYPE_PARAMETER_MULTIPART_BOUNDARY);
				parseMultipartRequest(charset, boundary, inputStream, htmlForm);
			}
			else
			{
				parseFormURLEncodedRequest(charset, readAsString(inputStream, charset), htmlForm);
			}

			// Also add the query parameters.

			MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters(false);
			for (Map.Entry<String, List<String>> entry : queryParameters.entrySet())
			{

				htmlForm.add(decode(entry.getKey(), charset), decode(entry.getValue(), charset));
			}

			return htmlForm;
		}
		catch (MessagingException e)
		{
			throw new WebApplicationException(e);
		}
	}


	private String determineCharset(Annotation[] annotations)
	{
		String charset = null;
		for (Annotation annotation : annotations)
		{
			if (annotation instanceof FormCharset)
			{
				charset = ((FormCharset)annotation).value();
				break;
			}
		}
		if (charset == null)
		{
			charset = (String)getAnnotationParameterDefault(FormCharset.class, "value");
		}
		return charset;
	}


	/**
	 * Get the default value of an annotation parameter.
	 *
	 * @param annotation the annotation to get a default value of
	 * @param parameter  the parameter to get the default value of
	 * @return the default value, or {@code null} if the parameter is required
	 */
	protected Object getAnnotationParameterDefault(Class<? extends Annotation> annotation, String parameter)
	{
		try
		{
			return annotation.getMethod(parameter).getDefaultValue();
		}
		catch (NoSuchMethodException e)
		{
			throw new IllegalStateException(format("Programmer error: the annotation %s has no parameter named '%s'",
			                                       annotation, parameter));
		}
	}


	private void parseMultipartRequest(String defaultCharset, String boundary, InputStream inputStream,
	                                   HTMLForm htmlForm) throws MessagingException, IOException
	{
		MIMEMessage mimeMessage = new MIMEMessage(inputStream, boundary, config);
		List<MIMEPart> parts = mimeMessage.getAttachments();
		for (MIMEPart part : parts)
		{
			// The content charset is optional.
			String contentEncoding = getFirstEncodedHeader(part, PART_ENCODING, PART_ENCODING_DEFAULT);
			InputStream partStream = MimeUtility.decode(part.readOnce(), contentEncoding);

			// The content disposition is required, and occurs once.
			String contentDispositionHeader = getFirstEncodedHeader(part, PART_DISPOSITION, null);
			ContentDisposition contentDisposition = new ContentDisposition(contentDispositionHeader);
			// There is always a part name, there may be a file name.
			String partName = decodeText(contentDisposition.getParameter(PART_DISPOSITION_PARTNAME));
			String fileName = contentDisposition.getParameter(PART_DISPOSITION_FILENAME);

			String contentType = getFirstEncodedHeader(part, PART_TYPE, PART_TYPE_DEFAULT);
			MediaType mediaType = MediaType.valueOf(contentType);
			if (fileName == null)
			{
				String charset = mediaType.getParameters().get(PART_TYPE_CHARSET);
				if (charset == null)
				{
					charset = defaultCharset;
				}
				String value = readAsString(partStream, charset);
				htmlForm.add(partName, value);
			}
			else
			{
				// First decode and fix the filename.
				fileName = fixFileNameBugIE(decodeText(fileName));

				UploadedFile uploadedFile = new UploadedFile(fileName, mediaType, partStream);
				htmlForm.addUploads(partName, uploadedFile);
			}
		}
	}


	private String getFirstEncodedHeader(MIMEPart part, String name, String defaultValue)
	{
		List<String> headerValues = part.getHeader(name);
		return headerValues == null ? defaultValue : headerValues.get(0);
	}


	private String readAsString(InputStream inputStream, String encoding) throws IOException
	{
		StringWriter buffer = new StringWriter();
		copy(new InputStreamReader(inputStream, encoding), buffer);
		return buffer.toString();
	}


	private void copy(Reader reader, Writer writer) throws IOException
	{
		char[] buffer = new char[1024];
		int charsRead;
		while ((charsRead = reader.read(buffer)) != -1)
		{
			writer.write(buffer, 0, charsRead);
		}
	}


	private String fixFileNameBugIE(String fileName)
	{
		// Old versions of Microsoft IE have a bug that sends along the entire path on the client machine,
		// i.e. "C:\Documents and Settings\George\My Documents\My Boss is a dickhead\Progress Report.xls".
		// Apart from being a security problem, this could get arkward. Thus, we strip the path.

		// Note:
		//    although \ is a legal file name character on Linux/OSX, it is also the escape for special shell
		//    characters. As such, we'll assume it will not occur in the file name.

		return fileName.substring(fileName.lastIndexOf('\\') + 1);
	}


	private void parseFormURLEncodedRequest(String charset, String query, HTMLForm htmlForm)
			throws UnsupportedEncodingException
	{
		for (String pair : query.split("&"))
		{
			int split = pair.indexOf('=');
			if (split != -1)
			{
				String name = decode(pair.substring(0, split), charset);
				String value = decode(pair.substring(split + 1), charset);
				htmlForm.add(name, value);
			}
		}
	}


	private String decode(String encodedValue, String charset) throws UnsupportedEncodingException
	{
		return URLDecoder.decode(encodedValue, charset);
	}


	private List<String> decode(List<String> encodedValues, String charset) throws UnsupportedEncodingException
	{
		List<String> result = new ArrayList<>(encodedValues.size());
		for (String encodedValue : encodedValues)
		{
			result.add(URLDecoder.decode(encodedValue, charset));
		}
		return result;
	}
}
