package cz.datalite.db;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DB helper.
 * 
 * @author pmarek
 */
public class DatabaseHelper {
	
	private static final Pattern TRIGGER_MESSAGE_PATTERN = Pattern.compile(".*ORA-2\\d{4}:\\s(.*)");
	
	/**
	 * Returns message from {@link SQLException} caused by DB trigger.
	 * <p>
	 * <u><b>E.g.:</b></u><br/>
	 * Caused by: java.sql.SQLException: ORA-20299: 1.1.2 - ORA-20110: cislo / provolba nema prirazene HOST_ID ORA-06512: na "NM.NMR_NEGEO",
	 * line 217 ORA-04088: chyba během provádění triggeru 'NM.NMR_NEGEO' <br/>
	 * <br/>
	 * <u><b>Returns:</b></u><br/>
	 * cislo / provolba nema prirazene HOST_ID
	 * </p>
	 * 
	 * @param e
	 */
	public static String getTriggerMessage(SQLException e) {
		Matcher m = TRIGGER_MESSAGE_PATTERN.matcher(e.getMessage());
		if (m.find() && m.groupCount() > 0) {
			return m.group(1);
		}
		return null;
	}

	/**
	 * Returns message from {@link SQLException} caused by DB trigger.
	 * <p>
	 * <u><b>E.g.:</b></u><br/>
	 * Caused by: java.sql.SQLException: ORA-20299: 1.1.2 - ORA-20110: cislo / provolba nema prirazene HOST_ID ORA-06512: na "NM.NMR_NEGEO",
	 * line 217 ORA-04088: chyba během provádění triggeru 'NM.NMR_NEGEO' <br/>
	 * <br/>
	 * <u><b>Returns:</b></u><br/>
	 * cislo / provolba nema prirazene HOST_ID
	 * </p>
	 * 
	 * @param e
	 */
	public static String getTriggerMessage(Exception e) {
		return (isSqlException(e) ? getTriggerMessage(getSqlException(e)) : null);
	}

	/**
	 * @return {@code true} in case of {@link SQLException}
	 */
	public static boolean isSqlException(Exception e) {
		Throwable cause = ExceptionUtils.getRootCause(e);
		return (e instanceof SQLException || cause instanceof SQLException);
	}

	/**
	 * Pokud je predana vyjimka nebo jeji pricina {@link SQLException}, vraci se {@code true}, jinak {@code null} pokud
	 * jde o jinou chybu.
	 * 
	 * @return {@code SQLException} if {@code e} is SQLException, {@code null} otherwise
	 */
	public static SQLException getSqlException(Exception e) {
		if (!isSqlException(e))
			return null;
		
		if (e instanceof SQLException)
			return (SQLException) e;
		
		return (SQLException) ExceptionUtils.getRootCause(e);
	}
	
}
