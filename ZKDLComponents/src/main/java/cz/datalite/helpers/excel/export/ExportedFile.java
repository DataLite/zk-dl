package cz.datalite.helpers.excel.export;

import org.zkoss.io.Files;

import java.io.InputStream;
import java.io.Serializable;

/**
 * File export
 */
public class ExportedFile implements Serializable {
	/**
	 * File name
	 */
	private final String filename;
	/**
	 * Content type of file content
	 */
	private final String contentType;
	/***
	 * Data itself - opened stream.
	 * @see #getByteData()
	 */
	private final InputStream data;

	/**
	 * Construct with name, format, content type and stream data (binary).
	 *
	 * <p>It tries to construct format and contentType from each other or name.
	 *
	 * @param filename    the name (usually filename); might be null.
	 * @param contentType the content type; might be null.
	 * @param data        the binary data; never null.
	 */
	public ExportedFile(String filename, String contentType, InputStream data) {
		if (data == null)
			throw new IllegalArgumentException("data is missing");
		this.filename = filename;
		this.contentType = contentType;
		this.data = data;
	}

	public String getFilename() {
		return filename;
	}

	public String getContentType() {
		return contentType;
	}

	public InputStream getData() {
		return data;
	}

	/**
	 * Get data content. Closes internal input stream!
	 * @return byte data
	 */
	public byte[] getByteData() {
		try (InputStream is = data){
			return Files.readAll(is);
		} catch (java.io.IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
}

