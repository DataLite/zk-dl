package cz.datalite.collection;

import java.util.Collection;

/**
 * Utility pro praci s kolekci.
 * Hled na {@code org.apache.commons.collections.*}!
 */
public class CollectionUtil {

	/**
	 * Zda kolekce obsahuje ne-null polozky.
	 * @param collection
	 * @return {@code false} pokud je kolekce prazdna nebo obsahuje pouze {@code null} polozky, jinak {@code true}
	 * @throws NullPointerException pokud je vstupni kolekce null
	 * @see cz.datalite.collection.ArrayUtil#containsNotNull(Object[])
	 */
	public static boolean containsNotNull(Collection<?> collection) throws NullPointerException {
		for (Object o : collection) {
			if (o != null) {
				return true;
			}
		}
		return false;
	}
}
