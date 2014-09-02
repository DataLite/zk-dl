package cz.datalite.collection;

import java.util.Collection;

/**
 * Utility pro praci s kolekci.
 * Hled na <code>org.apache.commons.collections.*</code>!
 */
public class CollectionUtil {

	/**
	 * Zda kolekce obsahuje ne-null polozky.
	 * @param collection
	 * @return <code>false</code> pokud je kolekce prazdna nebo obsahuje pouze <code>null</code> polozky, jinak <code>true</code>
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
