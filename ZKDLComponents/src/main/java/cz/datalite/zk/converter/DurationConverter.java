package cz.datalite.zk.converter;

import cz.datalite.helpers.StringHelper;
import org.joda.time.Duration;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.PeriodFormat;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Component;

import java.util.Locale;

/**
 * Converts millis time duration to human-readable format.
 */
public class DurationConverter implements Converter<String, Long, Component> {

	public static final String FORMAT_STANDARD_DAYS = "standardDays";

	@Override
	public String coerceToUi(Long beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		String format = (String) ctx.getConverterArg("format");
		return formatDuration(beanProp, format);
	}

	public static String formatDuration(Long beanProp, String format) {
		Locale l = Locales.getCurrent();
		ReadablePeriod period;
		if (FORMAT_STANDARD_DAYS.equals(format)) {
			period = new Duration(beanProp).toPeriod().toStandardDays();
		} else {
			period = new Duration(beanProp).toPeriod().normalizedStandard();
		}

		return PeriodFormat.wordBased(l).print(period);
	}

	@Override
	public Long coerceToBean(String compAttr, Component component, BindContext ctx) {
		if (StringHelper.isNull(compAttr)) {
			return null;
		}
		return Duration.parse(compAttr).getMillis();
	}

}
