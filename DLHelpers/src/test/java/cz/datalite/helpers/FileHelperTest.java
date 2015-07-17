package cz.datalite.helpers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *  Test pro {@link FileHelper}
 */
public class FileHelperTest {

	@Test
	public void testCleanFilename() throws Exception {

		assertNull(FileHelper.cleanFilename(null));

		assertEquals("file.ext", FileHelper.cleanFilename("file.ext"));
		assertEquals("file", FileHelper.cleanFilename("file"));
		assertEquals("file.tar.bz", FileHelper.cleanFilename("file.tar.bz"));
		assertEquals("f_file.tar.bz", FileHelper.cleanFilename("f_file.tar.bz"));

		assertEquals("_file.ext", FileHelper.cleanFilename("/file.ext"));
		assertEquals("C__file.ext", FileHelper.cleanFilename("C:\\file.ext"));
		assertEquals("_fil_e.ext", FileHelper.cleanFilename("/fil:e.ext"));
		assertEquals("file.ext", FileHelper.cleanFilename("filé.ext"));
		assertEquals("file.ext", FileHelper.cleanFilename("filé.ext"));
		assertEquals("_________________", FileHelper.cleanFilename("\\ / : * ? \" < > |"));
	}
}