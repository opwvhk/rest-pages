package net.sf.opk.rest.util;

import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class StandardDigestsTest
{
	@Test
	public void testMD2()
	{
		assertDigest(StandardDigests.MD2, "MD2", 16);
	}


	private void assertDigest(StandardDigests digest, String algorithm, int size)
	{
		MessageDigest messageDigest = digest.digest();
		assertEquals(algorithm, messageDigest.getAlgorithm());
		assertEquals(size, messageDigest.getDigestLength());
	}


	@Test
	public void testMD5()
	{
		assertDigest(StandardDigests.MD5, "MD5", 16);
	}


	@Test
	public void testSHA1()
	{
		assertDigest(StandardDigests.SHA1, "SHA-1", 20);
	}


	@Test
	public void testSHA256()
	{
		assertDigest(StandardDigests.SHA256, "SHA-256", 32);
	}


	@Test
	public void testSHA348()
	{
		assertDigest(StandardDigests.SHA348, "SHA-384", 48);
	}


	@Test
	public void testSHA512()
	{
		assertDigest(StandardDigests.SHA512, "SHA-512", 64);
	}


	@Test
	public void testValues()
	{
		Set<StandardDigests> allStandardDigests = new HashSet<>();
		Collections.addAll(allStandardDigests, StandardDigests.values());

		assertEquals(6, allStandardDigests.size());
		assertTrue(allStandardDigests.contains(StandardDigests.MD2));
		assertTrue(allStandardDigests.contains(StandardDigests.MD5));
		assertTrue(allStandardDigests.contains(StandardDigests.SHA1));
		assertTrue(allStandardDigests.contains(StandardDigests.SHA256));
		assertTrue(allStandardDigests.contains(StandardDigests.SHA348));
		assertTrue(allStandardDigests.contains(StandardDigests.SHA512));
	}


	@Test
	public void testValueOf()
	{
		assertEquals(StandardDigests.SHA512, StandardDigests.valueOf("SHA512"));
	}


	@Test(expected = InternalError.class)
	public void testErrorInJVM()
	{
		StandardDigests.createDigest("NonExistingDigest");
	}
}
