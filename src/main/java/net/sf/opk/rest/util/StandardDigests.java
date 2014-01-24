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
package net.sf.opk.rest.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.lang.String.format;


/**
 * Standard digests as per the JCE specification.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest">Java
 *      Cryptography Architecture API Specification & Reference, Appendix A (Standard Algorithm Names)</a>
 */
public enum StandardDigests
{
	/**
	 * The 128-bit (16 byte) MD2 digest.
	 */
	MD2("MD2"),
	/**
	 * The 128-bit (16 byte) MD5 digest.
	 */
	MD5("MD5"),
	/**
	 * The 160-bit (20 byte) SHA-1 digest.
	 */
	SHA1("SHA-1"),
	/**
	 * The 256-bit (32 byte) SHA-256 digest.
	 */
	SHA256("SHA-256"),
	/**
	 * The 384-bit (48 byte) SHA-384 digest.
	 */
	SHA348("SHA-384"),
	/**
	 * The 512-bit (64 byte) SHA-512 digest.
	 */
	SHA512("SHA-512");

	private String algorithm;


	private StandardDigests(String algorithm)
	{
		this.algorithm = algorithm;
	}


	/**
	 * Creates and returns a new digest.
	 *
	 * @return a MessageDigest
	 */
	public MessageDigest digest()
	{
		return createDigest(algorithm);
	}


	static MessageDigest createDigest(String algorithm)
	{
		try
		{
			return MessageDigest.getInstance(algorithm);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new InternalError(format("This JVM does not support the MessageDigest algorithm %s, as required " +
			                               "according to the Java Cryptography Architecture API Specification & " +
			                               "Reference, Appendix A", algorithm));
		}
	}
}
