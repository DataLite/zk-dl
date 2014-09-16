package cz.datalite.zk.converter;

import org.apache.commons.lang.StringUtils;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * Conversion Array -&gt; String
 * @see cz.datalite.zk.converter.CollectionConverter
 */
public class ArrayConverter
		implements Converter<String, Object[], Component> {

	@Override
	public String coerceToUi(Object[] beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		final String separator = (String) ctx.getConverterArg("separator");
		return StringUtils.join(beanProp, separator != null ? separator : ", ");
	}

	@Override
	public Object[] coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}
