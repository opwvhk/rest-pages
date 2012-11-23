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
package net.sf.opk.example;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.rest.forms.conversion.Converter;

import static java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;


/**
 * Example converter that trims all excess whitespace off Strings.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class TrimmingStringConverter implements Converter
{
	/**
	 * Regular expression to trim a value. Matches all strings, the trimmed value is the first/only group, and
	 * all-whitespace strings return a null group.
	 */
	private static final Pattern TRIMMED_VALUE = Pattern.compile("\\s*(\\S.*\\S|\\S)\\s*", UNICODE_CHARACTER_CLASS);


	@Override
	public boolean canConvertTo(ResolvedType resolvedType)
	{
		return resolvedType.getErasedType().equals(String.class);
	}


	@Override
	public <T> T convertTo(ResolvedType resolvedType, List<String> values)
	{
		if (values.isEmpty() || values.get(0) == null)
		{
			return null;
		}
		else
		{
			return (T)sanitize(values.get(0));
		}
	}


	static String sanitize(String text)
	{
		Matcher matcher = TRIMMED_VALUE.matcher(text);
		if (matcher.matches())
		{
			return matcher.group(1);
		}
		else
		{
			return null;
		}
	}
}
