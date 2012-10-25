package net.sf.opk.rest.pages;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.MediaType.TEXT_HTML_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ResourceForwardTest
{
	private ResourceForwardFilter filter;
	private RequestDispatcher requestDispatcher;
	private ResourceForwardWriter messageBodyWriter;
	private String PATH;

	private ResourceForward forward;


	@Before
	public void initialize()
	{
		filter = new ResourceForwardFilter();
		filter.init(mock(FilterConfig.class));

		requestDispatcher = mock(RequestDispatcher.class);

		messageBodyWriter = new ResourceForwardWriter();

		PATH = "/WEB-INF/hello.jsp";
		forward = new ResourceForward(PATH);
	}


	@After
	public void cleanUp()
	{
		filter.destroy();
	}


	@Test
	public void testAnnotations()
	{
		assertTrue(ResourceForwardWriter.class.isAnnotationPresent(Provider.class));
		Produces produces = ResourceForwardWriter.class.getAnnotation(Produces.class);
		assertNotNull(produces);
		assertEquals(1, produces.value().length);
		assertEquals(TEXT_HTML, produces.value()[0]);
	}


	@Test
	public void testIsWritable()
	{
		ResourceForward mock = mock(ResourceForward.class);
		Annotation[] none = new Annotation[0];

		assertTrue(messageBodyWriter.isWriteable(ResourceForward.class, ResourceForward.class, none, TEXT_HTML_TYPE));
		assertTrue(messageBodyWriter.isWriteable(mock.getClass(), mock.getClass(), none, TEXT_HTML_TYPE));

		assertFalse(messageBodyWriter.isWriteable(Object.class, Object.class, none, TEXT_HTML_TYPE));
		assertFalse(messageBodyWriter.isWriteable(String.class, String.class, none, TEXT_HTML_TYPE));
		assertFalse(messageBodyWriter.isWriteable(ResourceForwardWriter.class, ResourceForwardWriter.class, none,
		                                          TEXT_HTML_TYPE));
	}


	@Test
	public void testSize()
	{
		ResourceForward mock = mock(ResourceForward.class);
		Annotation[] none = new Annotation[0];

		assertEquals(-1, messageBodyWriter.getSize(mock, ResourceForward.class, ResourceForward.class, none,
		                                           TEXT_HTML_TYPE));
		assertEquals(-1, messageBodyWriter.getSize(mock, mock.getClass(), mock.getClass(), none, TEXT_HTML_TYPE));
	}


	@Test(expected = NullPointerException.class)
	public void testSkippedCacheCausesFailure1() throws IOException, ServletException
	{
		doTestForward(false, true, null);
	}


	private ServletRequest doTestForward(final boolean useHttpRequest, final boolean useHttpResponse,
	                                     Exception forwardException) throws IOException, ServletException
	{
		@SuppressWarnings("unchecked")
		final MultivaluedMap<String, Object> headers = mock(MultivaluedMap.class);
		final OutputStream outputStream = mock(OutputStream.class);

		final ServletRequest request = useHttpRequest ? mock(HttpServletRequest.class) : mock(ServletRequest.class);
		final ServletResponse response = useHttpResponse ? mock(HttpServletResponse.class) : mock(
				ServletResponse.class);
		when(request.getRequestDispatcher(PATH)).thenReturn(requestDispatcher);
		if (forwardException != null)
		{
			doThrow(forwardException).when(requestDispatcher).forward(same(request), same(response));
		}

		FilterChain chain = new FilterChain()
		{
			@Override
			public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse)
					throws IOException, ServletException
			{
				assertSame(servletRequest, request);
				assertSame(servletResponse, response);
				if (useHttpRequest && useHttpResponse)
				{
					assertNotNull(ResourceForwardFilter.requestCache.get());
					assertNotNull(ResourceForwardFilter.responseCache.get());
				}
				else
				{
					assertNull(ResourceForwardFilter.requestCache.get());
					assertNull(ResourceForwardFilter.responseCache.get());
				}

				messageBodyWriter.writeTo(forward, forward.getClass(), forward.getClass(), new Annotation[0],
				                          TEXT_HTML_TYPE, headers, outputStream);
			}
		};
		filter.doFilter(request, response, chain);

		//noinspection VariableNotUsedInsideIf
		if (forwardException == null)
		{
			verify(requestDispatcher).forward(same(request), same(response));
		}
		return request;
	}


	@Test(expected = NullPointerException.class)
	public void testSkippedCacheCausesFailure2() throws IOException, ServletException
	{
		doTestForward(true, false, null);
	}


	@Test
	public void testSuccessfulForward1() throws IOException, ServletException
	{
		doTestForward(true, true, null);
	}


	@Test
	public void testSuccessfulForward2() throws IOException, ServletException
	{
		String attr1 = "attr1";
		String attr2 = "attr2";
		String attr3 = "attr3";
		String attr4 = "attr4";
		Object value1 = new Object();
		Object value2 = new Object();
		Object value3 = new Object();
		Object value4 = new Object();
		forward.withAttribute(attr1, value1).withAttribute(attr2, value2);
		forward.withAttribute(attr3, value3).withAttribute(attr4, value4);

		ServletRequest request = doTestForward(true, true, null);

		verify(request).setAttribute(attr1, value1);
		verify(request).setAttribute(attr2, value2);
		verify(request).setAttribute(attr3, value3);
		verify(request).setAttribute(attr4, value4);
	}


	@Test(expected = IOException.class)
	public void testForwardFailure1() throws IOException, ServletException
	{
		doTestForward(true, true, new IOException("Oops"));
	}


	@Test(expected = WebApplicationException.class)
	public void testForwardFailure2() throws IOException, ServletException
	{
		doTestForward(true, true, new ServletException("Oops"));
	}
}
