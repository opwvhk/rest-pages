package net.sf.opk.rest.util;

/**
 * Dummy bean to test {@link BeanUtil} with.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
class DummyBean
{
	DummyBean()
	{
		// Nothing to do.
	}


	private DummyBean(int age)
	{
		this.age = age;
	}


	DummyBean(String name)
	{
		this.name = name.toString();
	}


	private String name;
	private int age;


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public int getAge()
	{
		return age;
	}


	public void setAge(int age)
	{
		this.age = age;
	}


	private void foo()
	{
		// Nothing to do.
	}


	public void bar()
	{
		throw new UnsupportedOperationException();
	}
}
