package cz.datalite.zk.converter;

import cz.datalite.utils.LabelsUtil;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * Enum converter.
 * @see cz.datalite.utils.LabelsUtil
 */
public class EnumConverter implements Converter<String, Object, Component> {

	@Override
	public String coerceToUi(Object beanProp, Component component, BindContext ctx) {
		return LabelsUtil.getLabel((Enum<?>) beanProp);
	}

	@Override
	public Enum coerceToBean(String compAttr, Component component, BindContext ctx) {
		throw new IllegalArgumentException("Converting " + compAttr + " on " + component + " is not implemented (yet?).");
	}
}
