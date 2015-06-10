package cz.datalite.zk.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

import java.util.regex.Pattern;

/**
 * Converter pro prepocet souradnic do formatu dle parametru style
 * <ul>
 *
 * <li>style = "DM" =&gt;  48° 7.72242'</li>
 * <li>style = "DMS" =&gt; 48° 59' 7.722" // default value</li>
 * </ul>
 */
public class CoordinateConverter implements Converter<String, Double, Component> {

	public static final String STYLE_DM = "DM";
	public static final String STYLE_DMS = "DMS";
	public static final String SYMBOL_DEGREE = "\u00b0";

	private static final String STYLE = "style";

	/**
	 * Text to append.
	 */
	private static final String APPEND = "append";

	protected static final Pattern PATTERN_SPACE = Pattern.compile(" ");

	protected static final String SYMBOL_MINUTES = "'";

	protected static final String SYMBOL_SECONDS_1 = "\"";

	protected static final String SYMBOL_SECONDS_2 = "''";

	@Override
	public String coerceToUi(Double beanProp, Component component, BindContext ctx) {
		if (beanProp == null) {
			return null;
		}
		String style = (String) ctx.getConverterArg(STYLE);
		String append = (String) ctx.getConverterArg(APPEND);
		final String result = convertDToDMS(beanProp, style);
		if (append != null) {
			return new StringBuilder(result).append(append).toString();
		}
		return result;
	}


	@Override
	public Double coerceToBean(String compAttr, Component component, BindContext ctx) {
		if (compAttr.isEmpty()) {
			return null;
		}
		String style = (String) ctx.getConverterArg(STYLE);
		String append = (String) ctx.getConverterArg(APPEND);
		if (append != null) {
			compAttr = compAttr.substring(append.length());
		}
		return convertDMSToD(compAttr, style);
	}

	public static String convertDToDMS(Double coordinate, String style) {
		double degrees = Math.floor(coordinate);
		double minutes = (coordinate - degrees) * 60;
		if (STYLE_DM.equals(style)) {
			minutes = (double) Math.round(minutes * 100000) / 100000;
			final String format = "%s" + SYMBOL_DEGREE + "%s" + SYMBOL_MINUTES;
			return String.format(format, (int) degrees, minutes);
		}
        else { // default ("DMS".equals(style))

			double seconds = (double) Math.round((minutes - Math.floor(minutes)) * 60 * 1000) / 1000;
			minutes = Math.floor(minutes);

			final String format = "%s" + SYMBOL_DEGREE + "%s" + SYMBOL_MINUTES + "%s" + SYMBOL_SECONDS_1;
			return String.format(format, (int) degrees, (int) minutes, seconds);
		}
	}

	public static double convertDMSToD(String coordinate, String style) {
		coordinate = PATTERN_SPACE.matcher(coordinate).replaceAll("");
		double degrees = Double.valueOf(coordinate.substring(0, coordinate.indexOf(SYMBOL_DEGREE)));
		double minutes = Double.valueOf(coordinate.substring(coordinate.indexOf(SYMBOL_DEGREE) + 1, coordinate.indexOf(SYMBOL_MINUTES))) / 60;
		double result;
		if (STYLE_DM.equals(style)) {
			return degrees + minutes;
		}
		else {
			try {
				double seconds;
				try {
					seconds = Double.valueOf(coordinate.substring(coordinate.indexOf(SYMBOL_MINUTES) + 1, coordinate.indexOf(SYMBOL_SECONDS_2))) / 3600;
				}
				catch (StringIndexOutOfBoundsException e)
				{
					seconds = Double.valueOf(coordinate.substring(coordinate.indexOf(SYMBOL_MINUTES) + 1, coordinate.indexOf(SYMBOL_SECONDS_1))) / 3600;
				}
				result = (degrees + minutes + seconds) * 10000000;
				result = Math.round(result);
				return result / 10000000;
			}
			catch (StringIndexOutOfBoundsException e)
			{
				return 0;
			}
		}
	}

}
