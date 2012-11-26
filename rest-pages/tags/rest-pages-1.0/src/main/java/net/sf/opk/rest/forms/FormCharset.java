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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>The character set to use when parsing submitted HTML forms into an {@link HTMLForm}. The character set can be
 * overridden in form fields when using {@code multipart/form-data}. Usually though, browsers use the character set as
 * defined in the HTML specification:</p>
 *
 * <ol>
 *
 * <li>using the {@code accept-charset} attribute of the form, or</li>
 *
 * <li>the character set of the page is the data doesn't fit, or</li>
 *
 * <li>UTF-8 as final fallback</li>
 *
 * </ol>
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 * @see <a href="http://www.w3.org/TR/html401/">The HTML specification, version 4.01</a>
 * @see <a href="http://dev.w3.org/html5/spec/single-page.html">The HTML specification, version 5 (as of 2012-10-21,
 *      this is a <strong>draft</strong></a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface FormCharset
{
	/**
	 * The character set to use to parse submitted HTML forms. The default value if the final fallback, {@code UTF-8}.
	 */
	String value() default "UTF-8";
}
