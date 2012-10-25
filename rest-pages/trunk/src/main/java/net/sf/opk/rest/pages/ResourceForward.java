package net.sf.opk.rest.pages;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;


/**
 * A description of a request forward as result of a JAX-RS resource.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class ResourceForward
{
	private String resourcePath;
	private Map<String, Object> attributes;


	/**
	 * Create a {@code ResourceForward}.
	 *
	 * @param resourcePath the path to forward to (should be in the same context)
	 */
	public ResourceForward(String resourcePath)
	{
		this.resourcePath = resourcePath;
		attributes = new HashMap<>();
	}


	/**
	 * Make an attribute available for the forwarded resource.
	 *
	 * @param name  the attribute name
	 * @param value the attribute value
	 * @return this {@code ResourceForward}; allows for fluent programming
	 */
	public ResourceForward withAttribute(String name, Object value)
	{
		attributes.put(name, value);
		return this;
	}


	/**
	 * Return the resource path. Used in {@link ResourceForwardFilter#forward(ResourceForward)} to do the actual request
	 * forward.
	 *
	 * @return the resource path to forward to
	 */
	String resourcePath()
	{
		return resourcePath;
	}


	/**
	 * Make the attributes available for the forwarded resource. Used in {@link ResourceForwardFilter#forward(ResourceForward)}.
	 */
	void supplyAttributes(HttpServletRequest request)
	{
		for (Map.Entry<String, Object> entry : attributes.entrySet())
		{
			request.setAttribute(entry.getKey(), entry.getValue());
		}
	}
}

