package cz.datalite.zk.converter;

import cz.datalite.helpers.StringHelper;
import cz.datalite.time.DateTimeUtil;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.util.Date;

/**
 * Date/time converter.
 * Format configuration key is defined by "format" argument.
 */
public abstract class DateTimeConverter implements Converter<String, Object, Component> {

	public static final String DEFAULT_FORMAT_CONF_KEY = "text.dateTime.format";

	@Override
	public String coerceToUi(Object beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		String format = (String) ctx.getConverterArg("format");
		Date date = beanProp instanceof Long ? new Date((Long)beanProp) : (Date) beanProp;
		return format(date, format);
	}

	public String format(Date date, String format) {
		return DateTimeUtil.formatDate(date, getConfigurationParameter(format == null ? "text.dateTime.format" : format));
	}

	public String format(Date date) {
		return format(date, null);
	}

	@Override
	public Date coerceToBean(String compAttr, Component component, BindContext ctx) {
		if (StringHelper.isNull(compAttr)) {
			return null;
		}
		String format = (String) ctx.getConverterArg("format");
		return DateTimeUtil.parseDate(compAttr, getConfigurationParameter(format == null ? DEFAULT_FORMAT_CONF_KEY : format)).getTime();
	}

	protected abstract String getConfigurationParameter(String key);
}
