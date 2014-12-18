package cz.datalite.utils;

import cz.datalite.exception.Problem;
import cz.datalite.exception.ProblemUtil;
import cz.datalite.helpers.StringHelper;
import org.zkoss.util.resource.Labels;

import static cz.datalite.localization.LocalizationUtil.getEnumLocalizationKey;

/**
 * Localization Utils
 * @see Labels
 */
public final class LabelsUtil {


	/**
	 * Find localization key for enum, FQN enum otherwise.
	 * @param e
	 * @return
	 * @see cz.datalite.localization.LocalizationUtil#getEnumLocalizationKey
	 */
	public static String getLabel(Enum<?> e) {
		if (e == null)
			return null;

		String localizationKey = getEnumLocalizationKey(e);
		String label = Labels.getLabel(localizationKey);
		if (StringHelper.isNull(label))
			return e.name();

		return label;
	}

	/**
	 * {@link Problem} localization key, FQN otherwise.
	 * @param problem
	 * @return
	 * @see ProblemUtil#getProblemKey(cz.datalite.exception.Problem)
	 */
	public static String getLabel(Problem problem) {
		if (problem == null)
			return null;

		String localizationKey = ProblemUtil.getProblemKey(problem);
		String label = Labels.getLabel(localizationKey);
		if (StringHelper.isNull(label))
			return localizationKey;

		return label;
	}

	/**
	 * Utility for creating .properties file.
	 * @param args
	 */
	public static void main(String[] args) {
		/*Enum[] e  = RimRequestType.values();
		System.out.println("# " + e[0].getClass().getSimpleName());
		for (Enum t : e) {
			System.out.println(t.getClass().getName() + "." + t.name() + "=" + WordUtils.capitalizeFully(t.name(), new char[]{'_'}).replaceAll("_", ""));
		} */
	}
}
