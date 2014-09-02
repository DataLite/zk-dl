package cz.datalite.xml;

import cz.datalite.exception.ProblemException;
import cz.datalite.xml.XmlUtil;
import org.apache.xerces.dom.DocumentImpl;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static org.junit.Assert.*;

public class XmlUtilTest {

	private static final String ENCODING = "UTF-8";
	private static final String XML_UNFORMATED = "<?xml version=\"1.0\"?>"
			// Taha z webu a vyrazne zdrzuje test
			// "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">"
			+ "<hibernate-mapping default-access=\"field\">"
			+ "<subclass name=\"org.jbpm.context.exe.variableinstance.HibernateLongInstance\" extends=\"org.jbpm.context.exe.VariableInstance\" discriminator-value=\"H\">"
			+ "<any name=\"value\" id-type=\"long\" cascade=\"save-update\"><column name=\"LONGIDCLASS_\" />"
			+ "<column name=\"LONGVALUE_\" />" + "</any>" + "</subclass>" + "  <test-cdata>" + "<![CDATA[\n"
			+ "      do {noop(); } while (true)" + "]]>" + "<![CDATA[\n" + "      //endless?" + "]]></test-cdata>" + "</hibernate-mapping>";

	// private static final String xmlUnformated =
	// "<?xml version=\"1.0\"?><hibernate-mapping default-access=\"field\"><subclass name=\"org.jbpm.context.exe.variableinstance.HibernateLongInstance\" extends=\"org.jbpm.context.exe.VariableInstance\" discriminator-value=\"H\"><any name=\"value\" id-type=\"long\" cascade=\"save-update\"><column name=\"LONGIDCLASS_\" /><column name=\"LONGVALUE_\" /></any></subclass></hibernate-mapping>";
	private static final String XML_FORMATED;

	private static Document DOCUMENT = null;

	static {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
		// sb.append("<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n");
		// sb.append("  \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n");
		sb.append("<hibernate-mapping default-access=\"field\">\n");
		sb.append("<subclass discriminator-value=\"H\" ");
		sb.append("extends=\"org.jbpm.context.exe.VariableInstance\" name=\"org.jbpm.context.exe.variableinstance.HibernateLongInstance\">\n");
		sb.append("<any cascade=\"save-update\" id-type=\"long\" name=\"value\">\n");
		sb.append("<column name=\"LONGIDCLASS_\"/>\n");
		sb.append("<column name=\"LONGVALUE_\"/>\n");
		sb.append("</any>\n");
		sb.append("</subclass>  ");
		sb.append("<test-cdata><![CDATA[\n");
		sb.append("      do {noop(); } while (true)]]><![CDATA[\n");
		sb.append("      //endless?]]></test-cdata>\n");
		sb.append("</hibernate-mapping>\n");
		XML_FORMATED = sb.toString();

		Document doc = new DocumentImpl();
		Node root = doc.createElement("root");
		doc.appendChild(root);
		Element elem = doc.createElement("elem");
		elem.setAttribute("b-attr1", "value2");
		elem.setAttribute("attr1", "value1");
		elem.setTextContent("content");
		root.appendChild(elem);
		DOCUMENT = doc;
	}

	@Test
	public void testCreateDOMDocumentPretty() throws ProblemException {

		assertNull(XmlUtil.domToString(XmlUtil.createDOMDocument(null), null, true));

		Document dom = XmlUtil.createDOMDocument(XML_UNFORMATED);

		XmlUtil.domToString(dom, null, true);

		try {
			XmlUtil.domToString(dom, "", true);
			fail("Invalid encoding not reported");
		} catch (ProblemException expected) {
			// ok
		}

		// Validace encoding nefunguje
		// try {
		// XmlUtil.domToString(dom, "abcdef", true);
		// fail("Invalid encoding not reported");
		// } catch (ProblemException expected) {
		// // ok
		// }

		assertEquals(XML_FORMATED, XmlUtil.domToString(dom, ENCODING, true).replace("\r\n", "\n"));
	}

	@Test
	public void testPrettyFormatXmlDocumentString() throws Exception {

		final String EXPECTED = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<root>\n<elem attr1=\"value1\" b-attr1=\"value2\">content</elem>\n</root>\n";

		String actual = XmlUtil.domToString(DOCUMENT, ENCODING, true);

		// Platform dependency
		actual = actual.replace("\r\n", "\n");
		assertEquals(EXPECTED, actual);

		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>", XmlUtil.domToString(new DocumentImpl(), null, false));
	}

	@Test
	public void testCreateDOMDocument() throws Exception {
		final String xml = "<root><elem attr=\"value\">content</elem></root>";
		Document doc = XmlUtil.createDOMDocument(xml);
		Node rootElement = doc.getFirstChild();
		assertEquals("root", rootElement.getNodeName());
		Node elem = rootElement.getFirstChild();
		assertEquals("elem", elem.getNodeName());
		assertEquals(1, elem.getAttributes().getLength());
		assertEquals("value", (elem.getAttributes().getNamedItem("attr")).getNodeValue());
		assertEquals("content", elem.getTextContent());

		Document emptyDoc = XmlUtil.createDOMDocument("");
		assertNull(emptyDoc);
	}

	@Test
	public void testFindNode() throws Exception {
		// dodelat lepsi testy? (spatne vstupy)
		Node found = XmlUtil.findNode(DOCUMENT, "/root/elem");
		assertEquals("elem", found.getNodeName());

		try {
			XmlUtil.findNode(DOCUMENT, "Strč prst skrz krk");
			fail();
		} catch (ProblemException expected) {
			// ok
		}
	}

	@Test
	public void testGetFirstElement() throws ProblemException {
		{
			final String xml = "<root><elem attr=\"value\">content</elem></root>";
			Document dom = XmlUtil.createDOMDocument(xml);
			Element element = XmlUtil.getFirstElement(dom);
			assertNotNull(element);
			assertEquals("root", element.getNodeName());
		}

		{
			final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><elem attr=\"value\">content</elem></root>";
			Document dom = XmlUtil.createDOMDocument(xml);
			Element element = XmlUtil.getFirstElement(dom);
			assertNotNull(element);
			assertEquals("root", element.getNodeName());
		}

		{
			final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--komentar--><root><elem attr=\"value\">content</elem></root>";
			Document dom = XmlUtil.createDOMDocument(xml);
			Element element = XmlUtil.getFirstElement(dom);
			assertNotNull(element);
			assertEquals("root", element.getNodeName());
		}
	}

	@Test
	public void testContainsElements() throws ProblemException {
		{
			final String xml = "<root><elem attr=\"value\">content</elem></root>";
			Document dom = XmlUtil.createDOMDocument(xml);
			assertTrue(XmlUtil.containsElements(dom.getChildNodes()));
		}

		{
			final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><elem attr=\"value\">content</elem></root>";
			Document dom = XmlUtil.createDOMDocument(xml);
			assertTrue(XmlUtil.containsElements(dom.getChildNodes()));
		}

		{
			final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--komentar--><root> <!--komentar--> <elem attr=\"value\">content</elem></root>";
			Document dom = XmlUtil.createDOMDocument(xml);
			assertTrue(XmlUtil.containsElements(dom.getChildNodes()));
			assertTrue(XmlUtil.containsElements(dom.getDocumentElement().getChildNodes()));
		}

		{
			final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--komentar--><root> <!--komentar--> </root>";
			Document dom = XmlUtil.createDOMDocument(xml);
			assertTrue(XmlUtil.containsElements(dom.getChildNodes()));
			assertFalse(XmlUtil.containsElements(dom.getDocumentElement().getChildNodes()));
		}
	}

}
