package cz.datalite.xml;

/**
 * Utility pro praci s XML pouze jako s textem. Melo by byt rychlejsi, ale nemusi byt naprosto spolehlive!
 * 
 * @author mstastny
 */
public final class XmlStringUtil {

	/**
	 * Odstrani namespace z elementu xml.
	 * 
	 * @param xmlString
	 * @return vycistene xml
	 * @author mkouba
	 */
	public static String removeAllNsPrefixes(String xmlString) {
		return xmlString.replaceAll("<[a-zA-Z0-9]*:", "<").replaceAll("</[a-zA-Z0-9]*:", "</");
	}

	/**
	 * Odstrani z XML dokumenty vsechny tagy.
	 * 
	 * @param document
	 * @return text dokumentu
	 * @author mkouba
	 */
	public static String removeTags(String document) {
		return document.replaceAll("<.*?>", "");
	}
}
