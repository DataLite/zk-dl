package cz.datalite.mime;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import cz.datalite.mime.MimeDetectionUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * Test detekce MIME typu.
 * 
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public class MimeDetectionUtilTest {

	/**
	 * @throws IOException
	 */
	@Test
	public void testMimeDetection() throws IOException {

		byte[] gifData = FileUtils.readFileToByteArray(new File("src/test/resources/thequickbrownfox.gif"));
		byte[] xmlData = FileUtils.readFileToByteArray(new File("src/test/resources/some.xml"));
		byte[] pdfData = FileUtils.readFileToByteArray(new File("src/test/resources/test.pdf"));

		String mimeGif1 = MimeDetectionUtil.resolveMimeType(gifData);
		String mimeGif2 = MimeDetectionUtil.resolveMimeType("thequickbrownfox.gif");
		String mimeXml1 = MimeDetectionUtil.resolveMimeType(xmlData);
		String mimeXml2 = MimeDetectionUtil.resolveMimeType("some.xml");
		String mimePdf1 = MimeDetectionUtil.resolveMimeType(pdfData);
		String mimePdf2 = MimeDetectionUtil.resolveMimeType("test.pdf");

		assertEquals("image/gif", mimeGif1);
		assertEquals("image/gif", mimeGif2);
		assertEquals("text/xml", mimeXml1);
		assertEquals("text/xml", mimeXml2);
		assertEquals("application/pdf", mimePdf1);
		assertEquals("application/pdf", mimePdf2);
	}

}
