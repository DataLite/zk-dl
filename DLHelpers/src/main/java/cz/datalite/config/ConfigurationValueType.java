package cz.datalite.config;

/**
 * Typ hodnoty konfiguracni polozky.
 * 
 * @author mstastny
 */
public enum ConfigurationValueType {

	BOOLEAN,
	INTEGER,
	LONG,
	STRING,
	BIGDECIMAL,
	/**
	 * ISO 8601 - ISODateTimeFormat#localDateOptionalTimeParser()
	 */
	DATETIME;

	/**
	 * Klic pro lokalizaci.
	 */
	public String getLocalizationKey() {
		return String.format("%s.%s", getClass().getName(), this.toString());
	}

}
