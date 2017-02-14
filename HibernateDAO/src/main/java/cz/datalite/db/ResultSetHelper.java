package cz.datalite.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Pomocnik pro praci s {@link ResultSet}
 */
public final class ResultSetHelper {

	private ResultSetHelper() {
		// only static
	}

	/**
	 * Retrieves the value of the designated column in the current row
	 * of this {@code ResultSet} object as
	 * an {@code Integer} in the Java programming language.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
	 * @return the column value; if the value is SQL {@code NULL}, the
	 * value returned is {@code null}
	 * @throws SQLException if the columnLabel is not valid;
	 *                      if a database access error occurs or this method is
	 *                      called on a closed result set
	 * @see ResultSet#getInt(String)
	 */
	public static Integer getInteger(ResultSet rs, String columnLabel) throws SQLException {
		final int rsInt = rs.getInt(columnLabel);
		return !rs.wasNull() ? rsInt : null;
	}

	/**
	 * Retrieves the value of the designated column in the current row
	 * of this {@code ResultSet} object as
	 * an {@code Integer} in the Java programming language.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL {@code NULL}, the
	 * value returned is {@code null}
	 * @throws SQLException if the columnIndex is not valid;
	 *                      if a database access error occurs or this method is
	 *                      called on a closed result set
	 * @see ResultSet#getInt(int)
	 */
	public static Integer getInteger(ResultSet rs, int columnIndex) throws SQLException {
		final int rsInt = rs.getInt(columnIndex);
		return !rs.wasNull() ? rsInt : null;
	}

	/**
	 * Retrieves the value of the designated column in the current row
	 * of this {@code ResultSet} object as
	 * a {@code Long} in the Java programming language.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL {@code NULL}, the
	 * value returned is {@code null}
	 * @throws SQLException if the columnIndex is not valid;
	 *                      if a database access error occurs or this method is
	 *                      called on a closed result set
	 */
	public static Long getLong(ResultSet rs, int columnIndex) throws SQLException {
		final long rsLong = rs.getLong(columnIndex);
		return !rs.wasNull() ? rsLong : null;
	}

	/**
	 * Retrieves the value of the designated column in the current row
	 * of this {@code ResultSet} object as
	 * a {@code Long} in the Java programming language.
	 *
	 * @param columnLabel the label for the column specified with the SQL AS clause.  If the SQL AS clause was not specified, then the label is the name of the column
	 * @return the column value; if the value is SQL {@code NULL}, the
	 * value returned is {@code null}
	 * @throws SQLException if the columnLabel is not valid;
	 *                      if a database access error occurs or this method is
	 *                      called on a closed result set
	 */
	public static Long getLong(ResultSet rs, String columnLabel) throws SQLException {
		final long rsLong = rs.getLong(columnLabel);
		return !rs.wasNull() ? rsLong : null;
	}
}
