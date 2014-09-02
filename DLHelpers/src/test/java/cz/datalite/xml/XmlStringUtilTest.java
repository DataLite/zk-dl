package cz.datalite.xml;

import cz.datalite.xml.XmlStringUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XmlStringUtilTest {

	@Test
	public void testRemoveAllNsPrefixes() {
		final String xmlWithNs = "<ns1:root><ns2:elem attr=\"v:a:l:u:e\">&lt;=:o)</ns2:elem></ns1:root>";
		String cleanedXml = XmlStringUtil.removeAllNsPrefixes(xmlWithNs);
		assertEquals("<root><elem attr=\"v:a:l:u:e\">&lt;=:o)</elem></root>", cleanedXml);
		assertEquals("", XmlStringUtil.removeAllNsPrefixes(""));
	}

	@Test
	public void testRemoveTags() {

		final String ORIGINAL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><elem attr1=\"value1\" b-attr1=\"value2\">content</elem></root>";
		final String EXPECTED = "content";

		assertEquals(EXPECTED, XmlStringUtil.removeTags(ORIGINAL));

		assertEquals("", XmlStringUtil.removeTags(""));
		assertEquals("Ne moc XML s tucnym textem.", XmlStringUtil.removeTags("<p>Ne moc XML s <b>tucnym</b> textem.</p>"));
	}

}
