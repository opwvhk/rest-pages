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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class UploadedFileTest
{
	byte[] FILE_CONTENTS1 = {0x46, 0x69, 0x6c, 0x65, 0x73, 0x69, 0x7a, 0x65, 0x20, 0x3d, 0x20, 0x31, 0x35, 0x62, 0x0a};
	byte[] FILE_CONTENTS2 = {0x46, 0x69, 0x6c, 0x65, 0x73, 0x69, 0x7a, 0x65, 0x3a, 0x20, 0x31, 0x34, 0x62, 0x0a};
	byte[] FILE_CONTENTS3 = {0x46, 0x69, 0x6c, 0x65, 0x3a, 0x20, 0x6e, 0x6f, 0x74, 0x20, 0x73, 0x61, 0x6d, 0x65, 0x0a};


	@Test
	public void testConstruction() throws IOException
	{
		String filename = "MyFile.txt";
		MediaType mimeType = MediaType.TEXT_PLAIN_TYPE;
		UploadedFile uploadedFile = new UploadedFile(filename, mimeType, new ByteArrayInputStream(FILE_CONTENTS1));

		assertEquals(filename, uploadedFile.getFileName());
		assertEquals(mimeType, uploadedFile.getMimeType());
		assertEquals(FILE_CONTENTS1.length, uploadedFile.getFileSize());
		assertArrayEquals(FILE_CONTENTS1, read(uploadedFile.getInputStream()));
		assertArrayEquals(FILE_CONTENTS1, uploadedFile.getContents());

		assertEquals("UploadedFile{fileName='MyFile.txt', mimeType=text/plain, size=15 bytes}",
		             uploadedFile.toString());
	}


	private static byte[] read(InputStream inputStream) throws IOException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[16];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1)
		{
			outputStream.write(buffer, 0, bytesRead);
		}
		return outputStream.toByteArray();
	}
}
