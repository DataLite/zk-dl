package cz.datalite.config;

import cz.datalite.config.ConfigurationKey;
import cz.datalite.config.ConfigurationSource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * pro unit test {@link cz.datalite.config.ConfigurationTest}
 */
public class DefaultConfigurationSource
		implements ConfigurationSource {

	@Override
	public BigDecimal getBigDecimal(ConfigurationKey key) {
		return null;
	}

	@Override
	public Integer getInteger(ConfigurationKey key) {
		return null;
	}

	@Override
	public Long getLong(ConfigurationKey key) {
		if ("neni".equals(key.getValue())) {
			return null;
		}
		return 1L;
	}

	@Override
	public void update() {
		// empty
	}

	@Override
	public String[] getStringArray(ConfigurationKey key) {
		return null;
	}

	@Override
	public Map<String, Object> getItems() {
		return null;
	}

	@Override
	public Boolean getBoolean(ConfigurationKey key) {
		return null;
	}

	@Override
	public Date getDate(ConfigurationKey key) {
		return null;
	}

	@Override
	public String getString(ConfigurationKey key) {
		return null;
	}

	@Override
	public int getPrecedence() {
		return 1;
	}
}
