package cz.datalite.localization;

/**
 * Utilita pro praci s lokalizaci
 */
public final class LocalizationUtil {

	private LocalizationUtil() {
		// only static
	}

	/**
	 * Vrati (obecny) lokalizacni klic pro enum
	 *
	 * @param e
	 * @return
	 */
	public static String getEnumLocalizationKey(Enum<?> e) {
		//Enumy zurnalovanych objektu pouzivaji klice, aby se nemusely prekladat dvakrat
		if (Localizable.class.isAssignableFrom(e.getClass()))
			return ((Localizable) e).getKey();
					
		return new StringBuilder(e.getDeclaringClass().getName()).append(".").append(e.name()).toString();
	}
}
