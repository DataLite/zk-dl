package cz.datalite.zk.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.util.Collection;
import java.util.Date;

/**
 * Converter, ktery odhadne, na co se ma prevest. Umi pouze z beany na UI.
 */
public class SmartConverter implements Converter<String, Object, Component> {

	@Override
	public String coerceToUi(Object beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		if (beanProp instanceof Boolean) {
			return new BooleanConverter().coerceToUi(beanProp, component, ctx);
		} else if (beanProp instanceof Enum) {
			return new EnumConverter().coerceToUi(beanProp, component, ctx);
		} else if (beanProp instanceof Date) {
			return new SimpleDateTimeConverter().coerceToUi((Date) beanProp, component, ctx);
		} else if (beanProp instanceof Collection) {
			return new CollectionConverter().coerceToUi((Collection<?>) beanProp, component, ctx);
		} else if (beanProp instanceof Object[]) {
			return new ArrayConverter().coerceToUi((Object[]) beanProp, component, ctx);
		}
		return beanProp.toString();
	}

	@Override
	public Object coerceToBean(String compAttr, Component component, BindContext ctx) {
		throw new IllegalArgumentException("Not implemented. Only one-direction converter, cannot convert " + compAttr  + " on " + component + ".");
	}
}
