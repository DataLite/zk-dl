package cz.datalite.zk.converter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Rozsireni {@link cz.datalite.zk.converter.CollectionConverter} o ziskani hodnoty property prvku.
 */
public class CollectionPropertyConverter implements Converter<String, Collection<?>, Component> {

	@Override
	public String coerceToUi(Collection<?> beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		final String separator = (String) ctx.getConverterArg("separator");
		final String property = (String) ctx.getConverterArg("property");
		if (property != null) {
			return toUI(beanProp, separator, property);

		}
		return StringUtils.join(beanProp, separator != null ? separator : ", ");
	}

	protected String toUI(Collection<?> beanProp, String separator, String property) {
		List<String> propValues = new LinkedList<>();
		for (Object bean : beanProp) {
			try {
				propValues.add(BeanUtils.getNestedProperty(bean, property));
			} catch (Exception e) {
				propValues.add(e.getMessage());
			}
		}
		return StringUtils.join(propValues, separator != null ? separator : ", ");
	}

	@Override
	public Collection<?> coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}
