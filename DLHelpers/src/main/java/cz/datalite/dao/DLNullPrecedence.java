package cz.datalite.dao;

/**
 * Defines precedence of null values within {@code ORDER BY} clause.
 * <p>
 * Partial copy of <code>org.hibernate.NullPrecedence</code>.
 * </p>
 */
public enum DLNullPrecedence {
	/**
	 * Null precedence not specified. Relies on the RDBMS implementation.
	 */
	NONE,

	/**
	 * Null values appear at the beginning of the sorted collection.
	 */
	FIRST,

	/**
	 * Null values appear at the end of the sorted collection.
	 */
	LAST;

	public static DLNullPrecedence parse(String type) {
		if ("none".equalsIgnoreCase(type)) {
			return DLNullPrecedence.NONE;
		} else if ("first".equalsIgnoreCase(type)) {
			return DLNullPrecedence.FIRST;
		} else if ("last".equalsIgnoreCase(type)) {
			return DLNullPrecedence.LAST;
		}
		return null;
	}
}
