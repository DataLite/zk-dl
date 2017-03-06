package cz.datalite.xml;

import cz.datalite.check.Checker;
import cz.datalite.exception.ProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility pro praci s XML.
 * 
 * @author pmarek
 */
public final class XmlUtil {

	private static final Logger logger = LoggerFactory.getLogger(XmlUtil.class);

	private static final String DEFAULT_ENCODING = "UTF-8";

	private static DocumentBuilder getDocBuilder() throws ProblemException {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			return docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error(e.toString(), e);
			throw new ProblemException(e, XmlProblem.PARSER_CONFIGURATION);
		}
	}

	public static Document createDom() throws ProblemException {
		return getDocBuilder().newDocument();
	}

	/**
	 * Prevede XML retezec na DOM dokument.
	 * 
	 * @param xml
	 *        retezec obsahujici XML nebo HTML
	 * @return DOM dokument
	 * 
	 * @throws ProblemException
	 *         pokud dojde k chybe pri prevodu
	 */
	public static Document createDOMDocument(String xml) throws ProblemException {

		if (Checker.isBlank(xml))
			return null;

		try {
			InputSource is = new InputSource(new StringReader(xml));
			return getDocBuilder().parse(is);

		} catch (IOException | SAXException e) {
			logger.error(e.toString(), e);
			throw new ProblemException(e, XmlProblem.PARSING, e.toString(), e.getMessage());
		}
	}

	/**
	 * Naformatuje XML dokument do retezce s kazdym elementem na nove radce.
	 * 
	 * @param xml
	 *        retezec obsahujici XML
	 * @param encoding
	 *        kodovani vystupu
	 * 
	 * @return naformatovane XML jako retezec v pozadovanem kodovani
	 * 
	 * @throws ProblemException
	 *         pokud dojde k chybe pri transformaci XML na formatovany text
	 */
	public static String prettyFormatXml(String xml, String encoding) throws ProblemException {
		return domToString(createDOMDocument(xml), encoding, true);
	}

	/**
	 * Prevede DOM na String s danym kodovanim.
	 * 
	 * @param node
	 * @param encoding muze byt null
	 * @param pretty
	 *        prida nove radky za elementy
	 * @return
	 * @throws ProblemException
	 */
	public static String domToString(Node node, String encoding, boolean pretty) throws ProblemException {

		if (node == null)
			return null;

		String usedEncoding = encoding == null ? DEFAULT_ENCODING : encoding;

		if (Checker.isBlank(usedEncoding)) {
			throw new ProblemException(XmlProblem.INVALID_ENCODING);
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		DOMSource source = new DOMSource(node);

		try (StringWriter writer = new StringWriter()) {
			StreamResult result = new StreamResult(writer);

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, usedEncoding);
			transformer.setOutputProperty(OutputKeys.INDENT, pretty ? "yes" : "no");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			transformer.transform(source, result);

			return writer.toString();

		} catch (Exception e) {
			logger.error(e.toString(), e);
			throw new ProblemException(e, XmlProblem.SERIALIZE_DOM);
		}
	}

	/**
	 * Najde Node podle XPath vyrazu
	 * 
	 * @param document
	 * @param xPathExpression
	 * @return uzel
	 * @throws ProblemException
	 * @author mstastny
	 */
	public static Node findNode(Node document, String xPathExpression) throws ProblemException {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			Node node = (Node) xpath.evaluate(xPathExpression, document, XPathConstants.NODE);
			return node;
		} catch (XPathExpressionException e) {
			logger.error(e.toString(), e);
			throw new ProblemException(e, XmlProblem.XPATH, e.toString());
		}
	}

	/**
	 * Preskoci uvodni komentare apod.
	 * 
	 * @param node
	 * @return prvni subelement (jediny - korenovy - element v dokumentu)
	 */
	public static Element getFirstElement(Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode instanceof Element) {
				return (Element) childNode;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param nodeList
	 * @return zda obsahuje elementy
	 */
	public static boolean containsElements(NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param date
	 *        Je-li {@code null}, pouzije se aktualni cas
	 * @return novou instanci {@link XMLGregorianCalendar} odpovidajici zadanemu casu
	 */
	public static XMLGregorianCalendar newXMLGregorianCalendar(Date date) {

		// Tento class cast by mel byt bezpecny
		GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();

		if (date != null)
			calendar.setTime(date);

		try {
			DatatypeFactory factory = DatatypeFactory.newInstance();
			return factory.newXMLGregorianCalendar(calendar);

		} catch (DatatypeConfigurationException e) {
			logger.error(e.toString(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Pozor, pouziti org.springframework.oxm.Unmarshaller muze by u asi 500 B XML asi 8x rychlejsi, nez tato implementace.
	 * 
	 * @param targetClass
	 * @param xml
	 * @return
	 * 
	 * see cz.datalite.jee.xml.XmlHelper#fromXml(String) in DLBusiness
	 */
	public static <T> T unmarshal(Class<T> targetClass, String xml) {

		try {
			// setup object mapper using specified class
			JAXBContext context = JAXBContext.newInstance(targetClass);
			// parse the XML and return a requested instance
			@SuppressWarnings("unchecked")
			T result = (T) context.createUnmarshaller().unmarshal(new StringReader(xml));

			return result;

		} catch (JAXBException e) {
			logger.error(e.toString(), e);
			throw new ProblemException(XmlProblem.PARSING, e);
		}
	}

	/**
	 * Vytvori z objektu oanotovaneho JAXB xml reprezentaci
	 * <p>
	 *     <em>Pozor:</em> nepoužívat v produkčním kódu, může způsobit memory-leak.
	 * </p>
	 * @param object
	 * @param <T>
	 * @return
	 * 
	 * @see cz.datalite.jee.xml.XmlHelper#toXml(Object) in DLBusiness
	 */
	public static <T> String marshal(T object) {
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller jaxbMarshaller = context.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(object, writer);
			writer.flush();
			return writer.toString();
		}catch (JAXBException e) {
			logger.error(e.toString(), e);
			throw new ProblemException(XmlProblem.SERIALIZE, e);
		}
	}

	public static Source getSource(String xml) {
		return new StreamSource(new StringReader(xml));
	}
}
