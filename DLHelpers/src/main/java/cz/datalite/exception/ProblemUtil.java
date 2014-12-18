package cz.datalite.exception;

import cz.datalite.check.Checker;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Utilita pro praci s {@link Problem}
 */
public class ProblemUtil {

	private static final Logger logger = LoggerFactory.getLogger(ProblemUtil.class);


	private ProblemUtil() {
		//utilita
	}

	public static void checkProblem(Map<Problem, List<Object>> problems) throws ProblemException {
		if (!problems.isEmpty()) {
			throw new ProblemException(problems, null);
		}
	}

	public static String getProblemKey(Problem problem) {
		if (problem != null) {
			return new StringBuilder(problem.getClass().getName()).append(".").append(problem.name()).toString();
		}
		return null;
	}

	/**
	 *
	 * @param pattern
	 * @param params
	 * @return
	 */
	public static String formatMessage(String pattern, Object[] params) {

		if (Checker.isBlank(pattern))
			return null;

		// Je-li sablona zadana, kontrolujeme parametry metody

		try {
			StringBuilder result = new StringBuilder();
			int fromIndex = 0;
			int toIndex;
			while ((toIndex = pattern.indexOf('{', fromIndex)) >= 0) {
				result.append(pattern.substring(fromIndex, toIndex));
				fromIndex = toIndex + 1;
				toIndex = pattern.indexOf('}', fromIndex);

				String argVal = getValue(pattern.substring(fromIndex, toIndex), params);
				result.append(argVal);

				fromIndex = toIndex + 1;
			}

			// Pripojit zbytek
			result.append(pattern.substring(fromIndex));
			return result.toString();
		} catch (Exception e) {
			logger.error(e.toString(), e);
			return "Wrong log pattern";
		}
	}

	private static String getValue(String path, Object[] params) {

		try {
			int dotPos = path.indexOf('.');
			int paramNumber;
			if (dotPos > 0) {
				paramNumber = Integer.parseInt(path.substring(0, dotPos));
				Object value = params[paramNumber];
				return value == null ? null : String.valueOf(PropertyUtils.getProperty(value, path.substring(dotPos + 1)));
			} else {
				paramNumber = Integer.parseInt(path);
				return String.valueOf(params[paramNumber]);
			}
		} catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException | NumberFormatException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException e) {
			logger.error(e.toString(), e);
			return "Wrong value specification";
		}
	}

}
