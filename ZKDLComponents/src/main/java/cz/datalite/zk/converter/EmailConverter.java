package cz.datalite.zk.converter;

import cz.datalite.helpers.StringHelper;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * jednotlivé emaily oddělené středníkem nebo crlf. Při uložení nahradit crlf středníkem
 */
public class EmailConverter implements Converter<String, String, Component> {

	private static final Pattern SPLIT_REGEX = Pattern.compile("[;,\n\r]");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");

	@Override
	public String coerceToUi(String beanProp, Component component, BindContext ctx) {
		return beanProp;// no change
	}

	@Override
	public String coerceToBean(String compAttr, Component component, BindContext ctx) {
		return coerceToBean(compAttr, component);
	}

	private static boolean isValid(String email) {
		return EMAIL_PATTERN.matcher(email).matches();
	}

	protected static String coerceToBean(String compAttr, Component component) {
		if (compAttr == null) {
			return null;
		}
		final String[] emails = SPLIT_REGEX.split(compAttr);
		Set<String> emailSet = new TreeSet<>();
		for (String e : emails) {
			final String email = e.trim();
			if (!StringHelper.isNull(email)) {
				final boolean valid = isValid(email);
				if (!valid) {
					throw new WrongValueException(component, Labels.getLabel("validation.email"));
				}
				emailSet.add(email);
			}
		}

		return emailSet.stream()
				.collect(Collectors.joining(";"));
//		return Joiner.on(";").join(emailSet);
	}


}
