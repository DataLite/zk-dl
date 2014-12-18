package cz.datalite.zk.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;

/**
 * Convert class name to String defined by FQN key.
 */
public class ClassNameConverter implements Converter<String, Object, Component> {

	@Override
	public String coerceToUi(Object beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		return Labels.getLabel(beanProp.getClass().getName()) != null ? Labels.getLabel(beanProp.getClass().getName()) : beanProp.getClass().getSimpleName();
	}

	@Override
	public Object coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}
