package cz.datalite.zk.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Converter for {@link java.math.BigDecimal} with percent character
 */
public class PercentConverter implements Converter<String, BigDecimal, Component> {

	@Override
	public String coerceToUi(BigDecimal beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		return MessageFormat.format("{0,number,#,##0.00'%'}", beanProp);
	}


	@Override
	public BigDecimal coerceToBean(String compAttr, Component component, BindContext ctx) {
		throw new IllegalArgumentException("Converting " + compAttr + " on " + component + " is not implemented.");
	}

}
