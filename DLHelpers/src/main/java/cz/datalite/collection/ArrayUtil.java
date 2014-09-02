package cz.datalite.collection;

import java.util.List;

/**
 * Utilitky pro pole
 *
 * @author jkalina
 */
public class ArrayUtil {

	/**
	 * Přidá elementy z dst do src, pokud v src nebyly.
	 * @param dest
	 * @param src
	 * @param <T>
	 */
	public static <T> void addNoExists(List<T> dest, List<T> src) {
		for (T o : src)
			if (!dest.contains(o))
				dest.add(o);
	}

	/**
	 * Do noveho pole zkopiruje prvky vstupniho pole. Pokud je prvkem pole, vlozi jeho prvky.
	 */
	public static Object[] expand(Object... params) {

		int length = params.length;
		for (Object object : params) {
			if (object instanceof Object[]) {
				length = length + ((Object[]) object).length - 1;
			}
		}

		Object[] result = new Object[length];
		int destIndex = 0;

		for (Object param : params) {
			if (param instanceof Object[]) {
				int srcLength = ((Object[]) param).length;
				System.arraycopy(param, 0, result, destIndex, srcLength);
				destIndex += srcLength;
			} else {
				result[destIndex++] = param;
			}
		}

		return result;
	}

	/**
	 * Zda pole obsahuje ne-null polozky.
	 * @param array
	 * @return <code>false</code> pokud je pole prazdne nebo obsahuje pouze <code>null</code> polozky, jinak <code>true</code>
	 * @throws NullPointerException pokud je vstupni pole null
	 * @see CollectionUtil#containsNotNull(java.util.Collection)
	 */
	public static boolean containsNotNull(Object[] array) {
		for (Object v : array) {
			if (v != null) {
				return true;
			}
		}
		return false;
	}
}
