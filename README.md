REST Pages
==========

REST Pages is a web fragment which provides (via CDI) a JAX-RS `MessageBodyReader` and `MessageBodyWriter` to extend a
REST webservice into an MVC application. A REST webservice already has a Model and Controllers (resources). REST Pages
links these to Views, by reading and writing two classes:

<dl>
 <dt><code>ResourceForward</code></dt><dd>
  <p>Links Controllers (resources) to Views. Using plain JAX-RS, these views are merely a JSON/XML/... representation of
  your Model.</p>
  <p>Using a <code>ResourceForward</code>, you can forward the request to a JSP page, servlet, or any other resource.
  This allows you to use much more complex Views.</p>
 </dd>
 <dt><code>HTMLForm</code></dt><dd>
  <p>Links Views to Controllers (resources). Using plain JAX-RS, all a view can do is send a JSON/XML/... representation
  of your Model. If you\'re using HTML forms, you must use some JavaScript magic to create and submit a JSON object out
  of it. Especially for nested objects, the amount of magic required is high.</p>
  <p>Using an <code>HTMLForm</code>, form fields are interpreted as property names on an object you specify. This allows
  you to do full and partial updates to your Model <em>in the same transaction</em>. This is an advantage over other
  frameworks, even Spring MVC, which insist on giving you an updated object &mdash; thus preventing you from loading,
  updating and storing an object in the same transaction. REST Pages allows you to keep your data integrety.</p>
 </dd>
</dl>


Request forwarding
------------------

Often touted as "the" feature that transforms JAX-RS into MVC, request forwarding is actually only half of it. But it is
the important half. It allows you to seperate your view logic from the resources that control how the request is
handled. This is valuable, and not (yet?) part of the JAX-RS specification.

Several solutions exist, but usually as part of a specific JAX-RS implementation like Jersey. REST pages is a pure
JavaEE implementation, and does not depend on any JAX-RS implementation. Again, this leaves you free to choose whatever
JAX-RS implementation you wish.


Form handling
-------------

REST Pages parses _any_ HTML form. Both `application/x-www-urlencoded` and `multipart/form-data`, with or without file
uploads, and for any character set. It delivers the result as an input object to your JAX-RS resources. Then, when _you_
choose, it applies the input values to an object of _your_ choice.

No magic, no implicit assumption that you must use optimistic locking to prevent data loss (especially the latter is
extremely common).

You are in control.



Basic usage
-----------

This section is demonstrated in an example project: [using-rest-pages][]

To use REST Pages in your project, you add this dependency to your `pom.xml`:

	<dependency>
		<groupId>net.sf.opk</groupId>
		<artifactId>rest-pages</artifactId>
		<version>1.0</version>
	</dependency>

If you have disabled annotations and web fragments (via the `web-app` attribute `metadata-complete="true"`), you\'ll
need to add the web fragment `restPages`, for example like so:

	<absolute-ordering>
		<!-- The web.xml is used first -->
		<name>restPages</name>
	</absolute-ordering>

This completes the setup.

The resource method below demonstrates what REST Pages gives you:

	@Path("/my/resource/{id}")
	@POST
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResourceForward processForm(@PathParam("id") Integer id, HTMLForm form)
	{
		MyResource model = new MyResource(id); // Load your model from the database here
		form.applyValuesTo(model);
		return new ResourceForward("").withAttribute("updatedModel", model);
	}

That\'s all!


Custom value conversions
------------------------

Custom value conversions are possible by implementing the `net.sf.opk.rest.forms.conversion.Converter` interface.
Optionally, you can inplement the `net.sf.opk.rest.util.Prioritized` interface to resolve conflicts between converters.

An example of this is the class `net.sf.opk.example.TrimmingStringConverter` in the example project:
[using-rest-pages][]


[using-rest-pages]: src/site/resources/using-rest-pages.zip "Minimal example that demonstrates how to use REST Pages"

