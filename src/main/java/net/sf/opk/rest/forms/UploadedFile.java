package net.sf.opk.rest.forms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.ws.rs.core.MediaType;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 * A simple representation of an uploaded file.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public class UploadedFile
{
	private final String fileName;
	private final File tempFile;
	private final MediaType mimeType;


	/**
	 * Create an uploaded file.
	 *
	 * @param fileName    the name of the file
	 * @param mimeType    the MIME type of the file
	 * @param inputStream a stream to read the file from
	 * @throws IOException when the file cannot be read
	 */
	public UploadedFile(String fileName, MediaType mimeType, InputStream inputStream) throws IOException
	{
		this.fileName = fileName;
		this.mimeType = mimeType;

		tempFile = File.createTempFile("UploadedFile", null);
		tempFile.deleteOnExit();
		Files.copy(inputStream, tempFile.toPath(), REPLACE_EXISTING);
	}


	/**
	 * Get the name of the uploaded file.
	 *
	 * @return the name of the file, as given to the constructor
	 */
	public String getFileName()
	{
		return fileName;
	}


	/**
	 * Get the MIME type of the uploaded file.
	 *
	 * @return the MIME type of the file, as given to the constructor
	 */
	public MediaType getMimeType()
	{
		return mimeType;
	}


	/**
	 * Get the size of the uploaded file.
	 *
	 * @return the file size
	 */
	public long getFileSize()
	{
		return tempFile.length();
	}


	/**
	 * Create and return an {@code InputStream} to read the file.
	 *
	 * @return an {@code InputStream} to read the uploaded file with
	 * @throws FileNotFoundException when the temporary file for the file upload has been deleted
	 */
	public InputStream getInputStream() throws FileNotFoundException
	{
		return new FileInputStream(tempFile);
	}


	/**
	 * Get the entire contents of the uploaded file.
	 *
	 * @return the contents of the uploaded file
	 * @throws IOException when the temporary file for the file upload has been deleted
	 */
	public byte[] getContents() throws IOException
	{
		return Files.readAllBytes(tempFile.toPath());
	}


	@Override
	public String toString()
	{
		return String.format("UploadedFile{fileName='%s', mimeType=%s, size=%d bytes}", fileName, mimeType,
		                     tempFile.length());
	}
}
