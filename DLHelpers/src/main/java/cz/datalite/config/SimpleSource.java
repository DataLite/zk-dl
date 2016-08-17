package cz.datalite.config;

import cz.datalite.time.DateTimeUtil;
import org.apache.commons.configuration.PropertyConverter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Jednoduchy konfiguracni zdroj, jehoz polozky se musi napred rucne naplnit.
 * 
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public class SimpleSource
		implements ConfigurationSource {

	private Map<String, Object> values = new HashMap<>();

	private int precedence = 0;

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Object putValue(ConfigurationKey key, Object value) {
		return values.put(key.getValue(), value);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object removeValue(ConfigurationKey key) {
		return values.remove(key.getValue());
	}

	@Override
	public int getPrecedence() {
		return precedence;
	}

	/**
	 * @param precedence
	 *        the precedence to set
	 */
	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}

	@Override
	public Map<String, Object> getItems() {
		return values;
	}

	@Override
	public void update() {
		// Nic
	}

	@Override
	public String getString(ConfigurationKey key) {

		Object item = getObject(key);

		if (item != null)
			return item.toString();
		return null;
	}

	@Override
	public String[] getStringArray(ConfigurationKey key) {

		Object item = getObject(key);

		if (item != null) {
			if (item instanceof String[])
				return (String[]) item;
			else if (item instanceof String)
				// POZOR separator se teoreticky muze zmenit, carka je standardni hodnota commons configurations
				return StringUtils.split((String) item, ",");
		}
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(ConfigurationKey key) {

		Object item = getObject(key);

		if (item != null)
			return PropertyConverter.toBigDecimal(item);
		return null;
	}

	@Override
	public Integer getInteger(ConfigurationKey key) {

		Object item = getObject(key);

		if (item != null)
			return PropertyConverter.toInteger(item);
		return null;
	}

	@Override
	public Long getLong(ConfigurationKey key) {

		Object item = getObject(key);

		if (item != null)
			return PropertyConverter.toLong(item);
		return null;
	}

	@Override
	public Boolean getBoolean(ConfigurationKey key) {

		Object item = getObject(key);

		if (item != null)
			return PropertyConverter.toBoolean(item);
		return null;
	}

	@Override
	public Date getDate(ConfigurationKey key) {

		Object item = getObject(key);

		if (item != null) {
			if (item instanceof Date)
				return (Date) item;
			else if (item instanceof String)
				return DateTimeUtil.parseISODateTime((String) item).getTime();
		}
		return null;
	}

	protected Object getObject(ConfigurationKey key) {
		return values.get(key.getValue());
	}

}
