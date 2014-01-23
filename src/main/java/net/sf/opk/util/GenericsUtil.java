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
package net.sf.opk.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeBindings;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;

import static java.lang.String.format;


/**
 * Utility class for generics.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public final class GenericsUtil
{
	/**
	 * Generics resolver for types (classes and interfaces).
	 */
	private static final TypeResolver TYPE_RESOLVER = new TypeResolver();
	/**
	 * Generics resolver for type members.
	 */
	private static final MemberResolver MEMBER_RESOLVER = new MemberResolver(TYPE_RESOLVER);
	/**
	 * Cache for resolved method return types.
	 */
	private static final Cache<ResolvedType, Map<Method, ResolvedType>> RETURN_TYPE_CACHE = new Cache<>();


	/**
	 * Resolve a type, specifying its type parameters.
	 *
	 * @param type           the erased type to resolve
	 * @param typeParameters the parameters to apply to the erased type
	 * @return the resolved type
	 */
	public static ResolvedType resolveType(Class<?> type, Class<?>... typeParameters)
	{
		return TYPE_RESOLVER.resolve(type, typeParameters);
	}


	/**
	 * Resolve a type, specifying its type parameters.
	 *
	 * @param type                the erased type to resolve
	 * @param firstTypeParameter  the first type parameter to apply to the erased type
	 * @param otherTypeParameters the other type parameters to apply to the erased type
	 * @return the resolved type
	 */
	public static ResolvedType resolveType(Class<?> type, ResolvedType firstTypeParameter,
	                                       ResolvedType... otherTypeParameters)
	{
		ResolvedType[] typeParameters = new ResolvedType[otherTypeParameters.length + 1];
		typeParameters[0] = firstTypeParameter;
		System.arraycopy(otherTypeParameters, 0, typeParameters, 1, otherTypeParameters.length);
		return TYPE_RESOLVER.resolve(type, typeParameters);
	}


	/**
	 * Resolve a method return type.
	 *
	 * @param type   the type the method was found on
	 * @param method the method to resolve
	 * @return the resolved return type
	 */
	public static ResolvedType resolveReturnType(ResolvedType type, Method method)
	{
		ResolvedType actualType;
		if (type.canCreateSubtype(method.getDeclaringClass()))
		{
			actualType = TYPE_RESOLVER.resolveSubtype(type, method.getDeclaringClass());
		}
		else
		{
			actualType = type;
		}

		Map<Method, ResolvedType> returnTypeByMethod = RETURN_TYPE_CACHE.get(actualType);
		if (returnTypeByMethod == null)
		{
			returnTypeByMethod = findReturnTypesByMethod(actualType);
			RETURN_TYPE_CACHE.put(actualType, returnTypeByMethod);
		}

		ResolvedType resolvedReturnType = returnTypeByMethod.get(method);
		if (resolvedReturnType == null)
		{
			throw new IllegalArgumentException(format("%s does not define a method %s", actualType, method));
		}
		return resolvedReturnType;
	}


	private static Map<Method, ResolvedType> findReturnTypesByMethod(ResolvedType type)
	{
		Map<Method, ResolvedType> returnTypeByMethod = new HashMap<>();

		ResolvedTypeWithMembers resolvedTypeWithMembers = MEMBER_RESOLVER.resolve(type, null, null);
		for (ResolvedMethod resolvedMethod : resolvedTypeWithMembers.getMemberMethods())
		{
			returnTypeByMethod.put(resolvedMethod.getRawMember(), resolvedMethod.getReturnType());
		}
		return returnTypeByMethod;
	}


	/**
	 * Get a specific type parameter from a resolved type. If the resolved type is used without type parameters, returns
	 * the first upper bound (unparameterized).
	 *
	 * @param resolvedType     the type for which to find the type parameters
	 * @param erasedSuperClass the superclass a find the type parameter of
	 * @param index            the index of the type parameter in {@code erasedSuperClass}
	 * @return the type parameter, or its first upper bound if not available
	 */
	public static ResolvedType findTypeParameter(ResolvedType resolvedType, Class<?> erasedSuperClass, int index)
	{
		// Notes:
		// - if erasedSuperClass is used without type parameters, we'll get an empty list.
		// - if erasedSuperClass is not assignable from a resolvedType, typeParameters will be null

		List<ResolvedType> typeParameters = resolvedType.typeParametersFor(erasedSuperClass);
		if (typeParameters == null)
		{
			throw new IllegalArgumentException(format("%s is not a subclass of %s", resolvedType, erasedSuperClass));
		}

		ResolvedType typeParameter;
		if (!typeParameters.isEmpty())
		{
			typeParameter = typeParameters.get(index);
		}
		else
		{
			// The type is used unparameterized. Find the type variable on the erased superclass.

			TypeVariable<? extends Class<?>>[] typeVariables = erasedSuperClass.getTypeParameters();
			if (typeVariables.length == 0)
			{
				throw new IllegalArgumentException(format("%s has no type parameters", erasedSuperClass));
			}
			else
			{
				TypeVariable<? extends Class<?>> typeVariable = typeVariables[index];
				Type firstUpperBound = typeVariable.getBounds()[0];
				typeParameter = TYPE_RESOLVER.resolve(firstUpperBound, TypeBindings.emptyBindings());
			}
		}
		return typeParameter;
	}


	/**
	 * Utility class: do not instantiate.
	 */
	private GenericsUtil()
	{
		// Nothing to do.
	}
}
