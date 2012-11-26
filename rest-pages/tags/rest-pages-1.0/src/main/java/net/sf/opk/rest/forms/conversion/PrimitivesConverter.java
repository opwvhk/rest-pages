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
package net.sf.opk.rest.forms.conversion;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import com.fasterxml.classmate.ResolvedType;

import net.sf.opk.rest.util.Prioritized;

import static net.sf.opk.rest.util.GenericsUtil.resolveType;


/**
 * A converter for primitive types.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class PrimitivesConverter implements Converter, Prioritized
{
	private static final Map<ResolvedType, ResolvedType> wrapperTypes;


	static
	{
		Map<ResolvedType, ResolvedType> wrapperTypesByPrimitiveType = new HashMap<>(8);
		wrapperTypesByPrimitiveType.put(resolveType(Boolean.TYPE), resolveType(Boolean.class));
		wrapperTypesByPrimitiveType.put(resolveType(Byte.TYPE), resolveType(Byte.class));
		wrapperTypesByPrimitiveType.put(resolveType(Short.TYPE), resolveType(Short.class));
		wrapperTypesByPrimitiveType.put(resolveType(Character.TYPE), resolveType(Character.class));
		wrapperTypesByPrimitiveType.put(resolveType(Integer.TYPE), resolveType(Integer.class));
		wrapperTypesByPrimitiveType.put(resolveType(Long.TYPE), resolveType(Long.class));
		wrapperTypesByPrimitiveType.put(resolveType(Float.TYPE), resolveType(Float.class));
		wrapperTypesByPrimitiveType.put(resolveType(Double.TYPE), resolveType(Double.class));
		wrapperTypes = Collections.unmodifiableMap(wrapperTypesByPrimitiveType);
	}


	private ConverterByValueOf converterByValueOf;


	/**
	 * Create a primitives converter.
	 *
	 * @param converterByValueOf a converter that uses the {@code valueOf(String)} method all wrapper types have
	 */
	@Inject
	public PrimitivesConverter(ConverterByValueOf converterByValueOf)
	{
		this.converterByValueOf = converterByValueOf;
	}


	@Override
	public boolean canConvertTo(ResolvedType resolvedType)
	{

		return resolvedType.isPrimitive();
	}


	@Override
	public <T> T convertTo(ResolvedType resolvedType, List<String> values)
	{
		ResolvedType resolvedWrapperType = wrapperTypes.get(resolvedType);
		return (T)converterByValueOf.convertTo(resolvedWrapperType, values);
	}


	@Override
	public int getPriority()
	{
		return Integer.MIN_VALUE + 1;
	}
}
