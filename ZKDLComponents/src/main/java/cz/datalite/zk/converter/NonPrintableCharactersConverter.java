package cz.datalite.zk.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.util.regex.Pattern;

/**
 * UC0071 - čištění netisknutelných a jiných znaků
 * <p>
 * Clean text input
 * <ul>
 * <li>chr(9)</li>
 * <li>chr(10)</li>
 * <li>chr(13)</li>
 * <li>others</li>
 * </ul>
 * </p>
 *
 * @see org.apache.commons.lang.StringUtils#deleteWhitespace(java.lang.String)
 */
public class NonPrintableCharactersConverter implements Converter<String, String, Component> {


	private static final String REGEX = "\\p{C}";
	private static final Pattern PATTERN = Pattern.compile(REGEX);

	@Override
	public String coerceToUi(String beanProp, Component component, BindContext ctx) {
		return beanProp;// no change
	}

	@Override
	public String coerceToBean(String compAttr, Component component, BindContext ctx) {
		return clean(compAttr);
	}

	protected static String clean(String input) {
		if (input == null) {
			return null;
		}
		return PATTERN.matcher(input).replaceAll("");
	}
}
