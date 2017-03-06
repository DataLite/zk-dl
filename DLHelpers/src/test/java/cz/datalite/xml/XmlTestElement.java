package cz.datalite.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Testovaci JAXB beana
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "element")
public class XmlTestElement {
	@XmlAttribute(name = "attr")
	private String attr;

	@XmlElement(name = "sub")
	private String sub;

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}
}