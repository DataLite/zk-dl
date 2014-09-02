package cz.datalite.exception;

import java.io.Serializable;

/**
 * Problem v logice. Kontrolovane vyjimky pouzivaji <code>Problem</code> k popisu chyby (vcetne lokalizacni zpravy).
 * 
 * @author mstastny
 */
public interface Problem
		extends Serializable {

	/**
	 * @return ma-li byt stacktrace utlumeny pri logovani
	 */
	public boolean isStackTraceMuted();

	/**
	 * Klíč problému (implementace mnohdy v {@link Enum#name()})
	 * @return
	 */
	public String name();
}
