package cz.datalite.dao.plsql;

/**
 * Convertes DB object value from/to bean property
 */
public interface Converter<DB, BEAN> {
	/**
	 * Converts daatabase value to bean value
	 *
	 * @param value
	 * @return
	 */
	BEAN fromDb(DB value);

}
