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
import javax.validation.Path;

import net.sf.opk.beans.util.Cache;

import static java.util.regex.Pattern.compile;


/**
 * JavaBean property parser.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class PropertyParser
{
	/**
	 * An empty/root property. It represents any object/bean as a property.
	 */
	public static final BeanProperty EMPTY_PROPERTY = new RootProperty();
	/**
	 * Regular expression pattern to match Java identifiers.
	 */
	private static final String PATTERN_IDENTIFIER = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
	/**
	 * Regular expression pattern to match numbers.
	 */
	private static final String PATTERN_NUMBER = "\\d+";
	/**
	 * Regular expression pattern to match strings surrounded by single quotes (').
	 */
	private static final String PATTERN_STRING1 = "'(?:\\\\|\\'|[^\\'])*'";
	/**
	 * Regular expression pattern to match strings surrounded by double quotes (").
	 */
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
	private static final Pattern PROPERTY_PATTERN = compile(String.format(
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


	/**
	 * Parse a property path.
	 *
	 * @param path the property path to parse
	 * @return the parsed property
	 * @throws BeanPropertyException when the property path is invalid
	 */
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

		BeanProperty result = EMPTY_PROPERTY;

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
			throw new BeanPropertyException(String.format("Not a property path; failed to parse: \"%s\"",
			                                              path.substring(tailIndex)));
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


	/**
	 * Format a bean property so that it can be parsed by this parser.
	 * Shorthand for <code>format(property.toPath())</code>.
	 *
	 * @param property the property to format as a property path
	 * @return the parsable property path
	 */
	public String format(BeanProperty property)
	{
		return format(property.toPath());
	}


	/**
	 * Format a path so that it can be parsed by this parser.
	 *
	 * @param path the property path to format
	 * @return the parsable property path
	 */
	public String format(Path path)
	{
		StringBuilder buffer = new StringBuilder();

		for (Path.Node node : path)
		{
			if (node.getName() != null)
			{
				buffer.append('.').append(node.getName());
			}

			if (node.getIndex() != null)
			{
				buffer.append('[').append(node.getIndex()).append(']');
			}
			else if (node.getKey() != null)
			{
				String escaped = String.valueOf(node.getKey()).replace("\\", "\\\\").replace("'", "\\'");
				buffer.append("['").append(escaped).append("']");
			}
		}

		return buffer.substring(1);
	}
}
