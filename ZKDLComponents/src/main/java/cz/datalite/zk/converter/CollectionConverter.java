package cz.datalite.zk.converter;

import org.apache.commons.lang.StringUtils;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.util.Collection;

/**
 * Collection -&gt; String conversion.
 */
public class CollectionConverter
		implements Converter<String, Collection<?>, Component> {

	@Override
	public String coerceToUi(Collection<?> beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		final String separator = (String) ctx.getConverterArg("separator");
		return StringUtils.join(beanProp, separator != null ? separator : ", ");
	}

	@Override
	public Collection<?> coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}
