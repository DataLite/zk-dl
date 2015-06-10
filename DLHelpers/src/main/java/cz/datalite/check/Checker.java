package cz.datalite.check;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Ruzne kontroly objektu.
 * 
 * @author mstastny
 */
public final class Checker {

	private Checker() {
		// utilita
	}

	/**
	 * Kontrola pole zda je null nebo prazdne. Ale pozor, nekontroluje, jestli
	 * je pole slozene pouze z {@code null} hodnot. To muze byt problem pri
	 * pouziti na DAO.
	 * 
	 * @param array
	 *            pole objektu
	 * 
	 * @return {@code true} pokud je vstupni parametr null nebo nema zadne
	 *         prvky
	 */
	public static <T> boolean isNullOrEmpty(T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Kontrola kolekce zda je null nebo prazdna. Ale pozor, nekontroluje,
	 * jestli je kolekce slozena pouze z {@code null} hodnot. To muze byt
	 * problem pri pouziti na DAO.
	 * 
	 * @param collection
	 *            kolekce objektu
	 * 
	 * @return {@code true} pokud je vstupni parametr null nebo nema zadne
	 *         prvky
	 */
	public static boolean isNullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * Kontrola mapy zda je null nebo prazdna.
	 * 
	 * @param map
	 *            mapa objektu
	 * 
	 * @return {@code true} pokud je vstupni parametr null nebo nema zadne
	 *         prvky
	 */
	public static boolean isNullOrEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * Kontrola zda je retezec prazdny ("") nebo null.
	 * 
	 * <pre>
	 * Checker.isNullOrEmpty(null)      = true
	 * Checker.isNullOrEmpty("")        = true
	 * Checker.isNullOrEmpty(" ")       = false
	 * Checker.isNullOrEmpty("bob")     = false
	 * Checker.isNullOrEmpty("  bob  ") = false
	 * </pre>
	 * 
	 * @param value
	 *            kontrolovany retezec, muze byt null
	 * 
	 * @return {@code true} pokud je retezec null nebo prazdny
	 * 
	 * @see #isBlank(String)
	 */
	public static boolean isNullOrEmpty(String value) {
		return value == null || value.length() == 0;
	}

	/**
	 * Kontrola zda je retezec prazdny (""), null nebo zda obsahuje pouze
	 * whitespace znaky.
	 * 
	 * <pre>
	 * Checker.isBlank(null)      = true
	 * Checker.isBlank("")        = true
	 * Checker.isBlank(" ")       = true
	 * Checker.isBlank("bob")     = false
	 * Checker.isBlank("  bob  ") = false
	 * </pre>
	 * 
	 * @param value
	 *            kontrolovany retezec, muze byt null
	 * 
	 * @return {@code true} pokud je retezec null, prazdny nebo obsahuje
	 *         pouze whitespace znaky
	 * 
	 * @see #isNullOrEmpty(String)
	 */
	public static boolean isBlank(String value) {
		return value == null || value.trim().length() == 0;
	}

	/**
	 * @deprecated Pouzivat {@link Objects#equals(Object, Object)}
	 * @param a
	 * @param b
	 * @return
	 */
	@Deprecated
	public static boolean equals(Object a, Object b) {

		return Objects.equals(a, b);
	}
}
