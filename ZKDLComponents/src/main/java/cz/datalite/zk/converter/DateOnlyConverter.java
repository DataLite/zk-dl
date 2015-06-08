package cz.datalite.zk.converter;

import org.apache.commons.lang.time.DateUtils;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * Converter stripping time when setting to bean.
 *
 * @see cz.datalite.zk.converter.DateTimeConverter
 * @see java.text.DateFormat
 */
public class DateOnlyConverter implements Converter<Date, Date, Component> {

	@Override
	public Date coerceToUi(Date beanProp, Component component, BindContext ctx) {
		return beanProp;
	}

	@Override
	public Date coerceToBean(Date compAttr, Component component, BindContext ctx) {
		if (compAttr == null) {
			return null;
		}
		return DateUtils.truncate(compAttr, Calendar.DATE);
	}
}
