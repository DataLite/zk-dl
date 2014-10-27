package cz.datalite.zk.converter;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * Trim text to specified length, followed by "..." characters
 */
public class AbbreviateConverter
		implements Converter<String, String, Component> {

	private static final int DEFAULT_LENGTH = 20;

	@Override
	public String coerceToUi(String beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		final Long maxWidth = (Long) ctx.getConverterArg("maxWidth");
		return StringUtils.abbreviate(beanProp, 0, maxWidth != null ? maxWidth.intValue() : DEFAULT_LENGTH);
	}

	@Override
	public String coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}
