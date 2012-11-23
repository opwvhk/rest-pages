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
package net.sf.opk.rest.pages;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;


/**
 * A {@code MessageBodyWriter} that forwards requests for REST calls that return a {@code ResourceForward}.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
@Provider
@Produces(MediaType.TEXT_HTML)
public class ResourceForwardWriter implements MessageBodyWriter<ResourceForward>
{

	@Override
	public long getSize(ResourceForward resourceForward, Class<?> type, Type genericType, Annotation[] annotations,
	                    MediaType mediaType)
	{
		return -1;
	}


	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return ResourceForward.class.isAssignableFrom(type);
	}


	/**
	 * <p>Delegates forwarding the request to {@link ResourceForwardFilter}. This is required because the actual request
	 * and response objects are required by a {@link RequestDispatcher}.</p>
	 *
	 * <p>This is not possible with CDI or (vanilla) JAX-RS, as they supply a proxy. Injecting a {@code
	 * ThreadLocal&lt;HttpServletRequest&gt;} and {@code ThreadLocal&lt;HttpServletRequest&gt;} is a Jersey extension, and
	 * thus not portable.</p>
	 *
	 * <p>The filter used does provide a standards compliant way to access the request and response objects.</p>
	 *
	 * @param resourceForward the {@code ResourceForward} to handle
	 * @param type            always {@code ResourceForward.class}
	 * @param genericType     always {@code ResourceForward.class}
	 * @param annotation      annotations on the method that returned the {@code ResourceForward} (ignored)
	 * @param mediaType       always {@code text/html}
	 * @param httpHeaders     HTTP headers that may be set (ignored)
	 * @param output          the content stream (ignored)
	 * @throws IOException             when the {@code ResourceForwardFilter} cannot forward the request
	 * @throws WebApplicationException when the {@code ResourceForwardFilter} cannot forward the request
	 * @see <a href="http://jersey.576304.n2.nabble.com/Thread-access-to-Context-objects-tp3880758p3880915.html">Jersey
	 *      mailing list: Thread-access-to-Context-objects</a>
	 * @see <a href="http://tech.lefedt.de/2010/11/an-mvc-extension-for-jersey-selecting-views-based-on-executed-controller-and-action">
	 *     Blog entry demonstrating an MVC extension for Jersey</a>
	 */
	@Override
	public void writeTo(ResourceForward resourceForward, Class<?> type, Type genericType, Annotation[] annotation,
	                    MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream output)
			throws IOException, WebApplicationException
	{
		try
		{
			ResourceForwardFilter.forward(resourceForward);
		}
		catch (ServletException e)
		{
			throw new WebApplicationException(e);
		}
	}
}
