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
	 * Delegates forwarding the request to {@link ResourceForwardFilter}. This is required because the actual request
	 * and response objects are required by a {@link RequestDispatcher}. This is not possible with CDI or JAX-RS, as
	 * they supply a proxy. A filter provides the required access to the request and response.
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
