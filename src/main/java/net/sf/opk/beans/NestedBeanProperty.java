package net.sf.opk.beans;

/**
 * ...
 *
 * @author <a href="mailto:owestra@bol.com">Oscar Westra van Holthe - Kind</a>
 */
public abstract class NestedBeanProperty extends BeanProperty
{
	/**
	 * Parent property. If this property is a nested property, the parent property handles all but the last segment.
	 */
	private final BeanProperty parent;


	protected NestedBeanProperty(BeanProperty parent)
	{
		this.parent = parent;
	}


	public <T> TypedValue<T> getTypedParentValue(Object javaBean)
	{
		return parent.getTypedValue(javaBean);
	}


	protected PathBuilder parentPathBuilder()
	{
		return parent.toPathBuilder();
	}
}
