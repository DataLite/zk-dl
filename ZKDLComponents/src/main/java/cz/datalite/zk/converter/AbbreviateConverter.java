package cz.datalite.zk.converter;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * Trim text to specified length.
 */
public class AbbreviateConverter
		implements Converter<String, String, Component> {

	private static final int DEFAULT_LENGTH = 20;

	@Override
	public String coerceToUi(String beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		final Integer length = (Integer) ctx.getConverterArg("length");
		return StringUtils.abbreviate(beanProp, 0, length != null ? length : DEFAULT_LENGTH);
	}

	@Override
	public String coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}
