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
package net.sf.opk.beans;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

import net.sf.opk.rest.forms.conversion.ConversionService;
import net.sf.opk.util.Cache;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;


/**
 * JavaBean property parser.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class PropertyParser
{
	private static final String PATTERN_IDENTIFIER = "[\\p{L}_$][\\p{L}\\p{N}_$]*";
	private static final String PATTERN_NUMBER = "\\d+";
	private static final String PATTERN_STRING1 = "'(?:\\\\|\\'|[^\\'])*'";
	private static final String PATTERN_STRING2 = "\"(?:\\\\|\\\"|[^\\\"])*\"";
	/**
	 * <p>Regular expression that matches all supported properties, one by one. The matched String is a valid property
	 * if:</p><ol>
	 *
	 * <li>it does not start with a dot, and</li>
	 *
	 * <li>immediately after the last successful match the end of the String was hit</li>
	 *
	 * </ol>
	 *
	 * <p>The pattern has 3 capturing groups, exactly one of which will be non-null upon a match. They are, in order: a
	 * property name, a list index and a map key. The map key is either a property name, or a single or double quoted
	 * string. In the strings, backslashes may be used to escape the quotes (' resp. ").</p>
	 *
	 * <p>Explanation:</p><pre>
	 * \\G                                            # Must match immediately from the start or the previous match
	 * (?:                                            # Matches several alternatives:
	 *   (?:\\A|(?<=.)\\.)                            # 1. following a dot iff not at the start of the input,
	 *   (identifier)                                 #    an identifier (captured),
	 *   |\\[(?:                                      # 2. or in square brackets:
	 *     (number)                                   #    - a literal list index (captured),
	 *     |(identifier|string_single|string_double)  #    - or a literal map key (captured)
	 *   )\\]
	 * )
	 * (?=\\.|\\[|\\z)                               # Immediately following the match (but not part of it) must be a
	 *                                               # dot or a square bracket (the first characters of each of the
	 *                                               # matches above). If not, all input must have been matched.
	 * </pre>
	 */
	private static final Pattern PROPERTY_PATTERN = compile(format(
			"\\G(?:(?:\\A|(?<=.)\\.)(%s)|\\[(?:(%s)|(%s|%s|%s))\\])(?=\\.|\\[|\\z)", PATTERN_IDENTIFIER, PATTERN_NUMBER,
			PATTERN_IDENTIFIER, PATTERN_STRING1, PATTERN_STRING2));
	/**
	 * Cache to store the parse results mapped by their respective inputs.
	 */
	private static Cache<String, BeanProperty> cache = new Cache<>();
	/**
	 * Conversion service; passed along to {@link MapKey} instances.
	 */
	private ConversionService conversionService;


	/**
	 * Create a property parser.
	 *
	 * @param conversionService conversion service used for map keys
	 */
	@Inject
	public PropertyParser(ConversionService conversionService)
	{
		this.conversionService = conversionService;
	}


	public BeanProperty parse(String path)
	{
		BeanProperty result = cache.get(path);
		if (result == null)
		{
			result = doParse(path);
			cache.put(path, result);
		}
		return result;
	}


	private BeanProperty doParse(String path)
	{
		checkPatternConstraints(path);

		BeanProperty result = new RootProperty();

		Matcher propertyMatcher = PROPERTY_PATTERN.matcher(path);
		int tailIndex = 0; // Used to verify we've parsed everything, and if not to show what wasn't parsed.
		while (propertyMatcher.find())
		{
			tailIndex = propertyMatcher.end();

			String propertyName = propertyMatcher.group(1);
			String listIndex = propertyMatcher.group(2);
			String mapKey = propertyMatcher.group(3);

			if (propertyName != null)
			{
				result = new NamedProperty(result, propertyName);
			}
			else if (listIndex != null)
			{
				result = new ListIndex(result, Integer.parseInt(listIndex));
			}
			else
			{
				mapKey = unescape(mapKey);
				result = new MapKey(conversionService, result, mapKey);
			}
		}

		checkParsingCompleted(path, tailIndex);

		return result;
	}


	private void checkPatternConstraints(String path)
	{
		if (path == null || path.length() == 0)
		{
			throw new BeanPropertyException("Cannot parse empty property paths.");
		}
		else if (path.charAt(0) == '.')
		{
			throw new BeanPropertyException("Not a property path: starts with a dot.");
		}
	}


	private void checkParsingCompleted(String path, int tailIndex)
	{
		if (tailIndex < path.length())
		{
			throw new BeanPropertyException(format("Not a property path; failed to parse: \"%s\"", path.substring(
					tailIndex)));
		}
	}


	private static String unescape(String stringLiteral)
	{
		char firstCharacter = stringLiteral.charAt(0);
		if (firstCharacter == '\'' || firstCharacter == '"')
		{
			return unescape(firstCharacter, stringLiteral);
		}
		else
		{
			return stringLiteral;
		}
	}


	private static String unescape(char escapedQuote, String stringLiteral)
	{
		stringLiteral = stringLiteral.substring(1, stringLiteral.length() - 1);
		stringLiteral = stringLiteral.replace("\\" + escapedQuote, String.valueOf(escapedQuote));
		stringLiteral = stringLiteral.replace("\\\\", "\\");
		return stringLiteral;
	}
}
