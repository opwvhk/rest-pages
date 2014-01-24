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
package net.sf.opk.beans.conversion;

import java.util.List;
import javax.ws.rs.QueryParam;

import com.fasterxml.classmate.ResolvedType;


/**
 * A converter that supports generics. All standard implementations together convert {@code List}s of {@code String}s to
 * arrays and collections as defined for {@link QueryParam}.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public interface Converter
{
	/**
	 * Determine whether the converter can convert to the specified type.
	 *
	 * @param resolvedType the type to convert to
	 * @return {@code true} if the converter can convert to the type; {@code false} otherwise
	 */
	boolean canConvertTo(ResolvedType resolvedType);

	/**
	 * Convert the form values to the given type. All values belong to the same field.
	 *
	 * @param resolvedType the type to convert to
	 * @param values       the values to convert
	 * @return the converted value
	 */
	<T> T convertTo(ResolvedType resolvedType, List<String> values);
}
