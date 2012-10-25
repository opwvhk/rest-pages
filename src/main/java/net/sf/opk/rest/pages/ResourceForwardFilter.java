package net.sf.opk.rest.pages;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Filter to provide the {@link ResourceForwardWriter} with the real requests and responses provided by the container.
 * This is needed because they provide more functionality that is available via a CDI/JAX-RS provided proxies of {@link
 * HttpServletRequest} and {@link HttpServletResponse}.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
@WebFilter("/*")
public class ResourceForwardFilter implements Filter
{
	/**
	 * A thread-based cache for the HTTP requests handled by this filter. Required for the method {@link
	 * #forward(ResourceForward)}.
	 *
	 * Ideally, it would be private. But we also want to test if it's used.
	 */
	static ThreadLocal<HttpServletRequest> requestCache = new ThreadLocal<>();
	/**
	 * A thread-based cache for the HTTP reqponses handled by this filter. Required for the method {@link
	 * #forward(ResourceForward)}.
	 *
	 * Ideally, it would be private. But we also want to test if it's used.
	 */
	static ThreadLocal<HttpServletResponse> responseCache = new ThreadLocal<>();


	/**
	 * Forward the current request (i.e. the one handled by the current thread) as specified.
	 *
	 * @param resourceForward a specification of the request forward to perform
	 * @throws IOException      when forwarding fails due to an I/O error
	 * @throws ServletException when forwarding fails for another reason
	 */
	public static void forward(ResourceForward resourceForward) throws IOException, ServletException
	{
		HttpServletRequest request = requestCache.get();
		HttpServletResponse response = responseCache.get();

		resourceForward.supplyAttributes(request);
		RequestDispatcher dispatcher = request.getRequestDispatcher(resourceForward.resourcePath());
		dispatcher.forward(request, response);
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse)
		{
			requestCache.set((HttpServletRequest)request);
			responseCache.set((HttpServletResponse)response);
		}

		try
		{
			chain.doFilter(request, response);
		}
		finally
		{
			requestCache.set(null);
			responseCache.set(null);
		}
	}


	@Override
	public void init(FilterConfig config)
	{
		// Nothing to do.
	}


	@Override
	public void destroy()
	{
		// Nothing to do.
	}
}
