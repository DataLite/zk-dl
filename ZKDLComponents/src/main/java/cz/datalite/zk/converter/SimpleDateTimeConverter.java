package cz.datalite.zk.converter;

import cz.datalite.time.DateTimeUtil;
import org.joda.time.LocalDate;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Component;

import java.text.DateFormat;
import java.util.Date;

/**
 * @see cz.datalite.zk.converter.DateTimeConverter
 * @see java.text.DateFormat
 */
public class SimpleDateTimeConverter implements Converter<String, Date, Component> {

	@Override
	public String coerceToUi(Date beanProp, Component component, BindContext ctx) {
		String format = (String) ctx.getConverterArg("format");
		if (format != null) {
			return DateTimeUtil.formatDate(beanProp, format);
		}

		String style = (String) ctx.getConverterArg("style");
		return DateTimeUtil.formatDateTimeByLocale(beanProp, Locales.getCurrent(), convertToStyle(style));
	}

	@Override
	public Date coerceToBean(String compAttr, Component component, BindContext ctx) {
		LocalDate localDate = new LocalDate(compAttr);
		return localDate.toDate();
	}

	public int convertToStyle(String style) {
		if ("FULL".equals(style)) {
			return DateFormat.FULL;
		}
		if ("LONG".equals(style)) {
			return DateFormat.LONG;
		}
		if ("MEDIUM".equals(style)) {
			return DateFormat.MEDIUM;
		}
		if ("SHORT".equals(style)) {
			return DateFormat.SHORT;
		}
		return DateFormat.DEFAULT;
	}
}
