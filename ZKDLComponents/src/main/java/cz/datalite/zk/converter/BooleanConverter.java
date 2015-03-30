package cz.datalite.zk.converter;

import cz.datalite.helpers.StringHelper;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;

/**
 * Boolean -> localized String ("yes"/"no"/"nil" Labels or keys defined by "yesLabel"/"noLabel"/"nilLabel" params.)
 * @see Labels#getLabel(String)
 *
 */
public class BooleanConverter implements Converter<String, Object, Component> {

	@Override
	public String coerceToUi(Object beanProp, Component component, BindContext ctx) {
		return label(beanProp, ctx);
	}

	protected String label(Object beanProp, BindContext ctx) {
		if (beanProp == null) {
			final String nilLabel = (String) ctx.getConverterArg("nilLabel");
			return !StringHelper.isNull(nilLabel) ? nilLabel : Labels.getLabel("nil", (String)null);
		}
		if (Boolean.TRUE.equals(beanProp)) {
			final String yesLabel = (String) ctx.getConverterArg("yesLabel");
			return !StringHelper.isNull(yesLabel) ? yesLabel : Labels.getLabel("yes", "True");
		} else {
			final String noLabel = (String) ctx.getConverterArg("noLabel");
			return !StringHelper.isNull(noLabel) ? noLabel : Labels.getLabel("no", "False");
		}
	}

	@Override
	public Boolean coerceToBean(String compAttr, Component component, BindContext ctx) {
		return null;
	}
}
