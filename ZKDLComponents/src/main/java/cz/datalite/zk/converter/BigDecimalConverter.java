package cz.datalite.zk.converter;

import cz.datalite.helpers.StringHelper;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Converter for {@link java.math.BigDecimal} to and from {@link java.lang.String}
 */
public class BigDecimalConverter implements Converter<String, BigDecimal, Component> {

	@Override
	public String coerceToUi(BigDecimal beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		return format(beanProp);
	}

	public static String format(BigDecimal beanProp) {
		return MessageFormat.format("{0,number,#,##0.00}", beanProp);
	}


	@Override
	public BigDecimal coerceToBean(String compAttr, Component component, BindContext ctx) {
		if (StringHelper.isNull(compAttr)) {
			return null;
		}
		final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locales.getCurrent());
		decimalFormat.setParseBigDecimal(true);
		try {
			return (BigDecimal) decimalFormat.parse(compAttr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
