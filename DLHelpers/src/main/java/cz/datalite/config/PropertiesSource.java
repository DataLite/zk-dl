package cz.datalite.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Konfiguracni zdroj prebirajici konfiguraci ze system properties.
 * @see System#getProperties
 * @see System#getProperty
 */
public class PropertiesSource
		extends SimpleSource
		implements ConfigurationSource {

	public PropertiesSource() {
	}

	public PropertiesSource(int precedence) {
		setPrecedence(precedence);
	}

	@Override
	public Map<String, Object> getItems() {
		Set<Object> keys = System.getProperties().keySet();
		Map<String, Object> map = new HashMap<>();
		keys.stream().map(String::valueOf).forEach(k -> map.put(k, System.getProperty(k)));
		return map;
	}

	@Override
	protected Object getObject(ConfigurationKey key) {
		return System.getProperty(key.getValue());
	}
}
