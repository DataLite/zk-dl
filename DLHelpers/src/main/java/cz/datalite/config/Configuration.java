package cz.datalite.config;

import cz.datalite.check.Checker;
import cz.datalite.exception.ProblemException;
import cz.datalite.time.DateTimeUtil;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * Singleton - jednotna konfigurace aplikace. Vlastni hodnoty konfiguracnich polozek se prebiraji ze zdroju - {@link ConfigurationSource},
 * ktere jsou serazeny podle priority - {@link ConfigurationSource#getPrecedence()}. Zdroje s vyssi prioritou jsou razeny pred zdroje s
 * nizsi prioritou.
 * <p/>
 * <p>
 * Konfigurace neresi konkurencni pristupy, to maji na starosti zdroje.
 * </p>
 *
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public class Configuration {

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

	private static final Configuration instance = new Configuration();

	private final List<ConfigurationSource> sources = new ArrayList<>();

	private Date lastUpdate;

	/**
	 * Privatni konstruktor
	 */
	private Configuration() {
		super();
	}

	/**
	 * @return instance konfigurace
	 */
	public static Configuration instance() {
		return instance;
	}

	public static ConfigurationSource loadConfigFile(String fileName, int precedence) {
		try {
			ImmutableSource configuration = new ImmutableSource(precedence);
			org.apache.commons.configuration.Configuration p = new PropertiesConfiguration(fileName);
			configuration.addConfiguration(p);
			return configuration;
		} catch (ConfigurationException e) {
			throw new ProblemException(e, ConfigProblem.FILE_LOAD);
		}
	}

	/**
	 * @param source konfiguracni zdroj
	 */
	public void addSource(ConfigurationSource source) {

		logger.info("Add config source [impl: {}, precedence: {}]", source.getClass(), source.getPrecedence());

		source.update();
		lastUpdate = DateTimeUtil.now();

		sources.add(source);
		Collections.sort(sources, new ConfigurationSourceComparator());
	}

	/**
	 * Vycisti zdroje konfigurace.
	 */
	public void clearSources() {
		logger.info("Clearing sources...");
		sources.clear();
		lastUpdate = null;
	}

	private <T> T getObject(ConfigurationKey key, Class<T> clazz, T defaultValue) {
		try {
			return getObject(key, clazz);
		} catch (ProblemException e) {
			if (ConfigProblem.NOT_FOUND.equals(e.getProblem())) {
				return defaultValue;
			}
			throw e;
		}
	}

	/**
	 * Najde konfiguracni polozku daneho typu dle klice
	 *
	 * @param key   klic
	 * @param clazz dany typ, dle {@link cz.datalite.config.ConfigurationSource}, {@link cz.datalite.config.ConfigurationValueType}
	 * @param <T>   clazz
	 * @return hodnota konfiguracni polozky
	 */
	@SuppressWarnings("unchecked")
	private <T> T getObject(ConfigurationKey key, Class<T> clazz) {
		if (key == null) {
			return null;
		}

		T value;

		for (ConfigurationSource source : sources) {
			if (String.class.equals(clazz)) {
				value = (T) source.getString(key);
			} else if (String[].class.equals(clazz)) {
				value = (T) source.getStringArray(key);
			} else if (Long.class.equals(clazz)) {
				value = (T) source.getLong(key);
			} else if (Integer.class.equals(clazz)) {
				value = (T) source.getInteger(key);
			} else if (BigDecimal.class.equals(clazz)) {
				value = (T) source.getBigDecimal(key);
			} else if (Boolean.class.equals(clazz)) {
				value = (T) source.getBoolean(key);
			} else if (Date.class.equals(clazz)) {
				value = (T) source.getDate(key);
			} else {
				throw new IllegalArgumentException("Unknow datatype: " + clazz);
			}
			if (value != null) {
				return value;
			}
		}
		throw new ProblemException(ConfigProblem.NOT_FOUND, "ConfigItem with " + key + " was not found.", key);
	}

	/**
	 * Vrati hodnotu konfiguracni polozky dle zadaneho klice
	 *
	 * @param key klic konfiguracni polozky
	 * @return hodnota konfiguracni polozky
	 * @throws cz.datalite.exception.ProblemException pokud nebyla konfiguracni polozka nalezena
	 */
	public String getString(ConfigurationKey key) {
		return getObject(key, String.class);
	}


	/**
	 * Vrati hodnotu konfiguracni polozky dle zadaneho klice
	 *
	 * @param key          klic konfiguracni polozky
	 * @param defaultValue hodnota konf. polozky, pokud neexistuje.
	 * @return hodnota konfiguracni polozky
	 * @throws cz.datalite.exception.ProblemException pokud nebyla konfiguracni polozka nalezena
	 */
	public String getString(ConfigurationKey key, String defaultValue) {
		return getObject(key, String.class, defaultValue);
	}

	/**
	 * Vrati hodnotu konfiguracni polozky dle zadaneho klice
	 *
	 * @param key klic konfiguracni polozky
	 * @return hodnota konfiguracni polozky
	 * @throws cz.datalite.exception.ProblemException pokud nebyla konfiguracni polozka nalezena
	 */
	public String getString(String key) {
		return !Checker.isBlank(key) ? getString(new VirtualConfigurationKey(key)) : null;
	}

	public String getString(String key, String defaultValue) {
		return !Checker.isBlank(key) ? getString(new VirtualConfigurationKey(key), defaultValue) : null;
	}

	/**
	 * Vrati hodnotu konfiguracni polozky dle zadaneho klice
	 *
	 * @param key klic konfiguracni polozky
	 * @return hodnota konfiguracni polozky
	 * @throws cz.datalite.exception.ProblemException pokud nebyla konfiguracni polozka nalezena
	 */
	public String[] getStringArray(ConfigurationKey key) {
		return getObject(key, String[].class);
	}

	public String[] getStringArray(ConfigurationKey key, String[] defaultValue) {
		return getObject(key, String[].class, defaultValue);
	}

	/**
	 * Vrati hodnotu konfiguracni polozky dle zadaneho klice
	 *
	 * @param key klic konfiguracni polozky
	 * @return hodnota konfiguracni polozky
	 * @throws cz.datalite.exception.ProblemException pokud nebyla konfiguracni polozka nalezena
	 */
	public BigDecimal getBigDecimal(ConfigurationKey key) {
		return getObject(key, BigDecimal.class);
	}

	public BigDecimal getBigDecimal(ConfigurationKey key, BigDecimal defaultValue) {
		return getObject(key, BigDecimal.class, defaultValue);
	}

	/**
	 * Vrati hodnotu konfiguracni polozky dle zadaneho klice
	 *
	 * @param key klic konfiguracni polozky
	 * @return hodnota konfiguracni polozky
	 * @throws cz.datalite.exception.ProblemException pokud nebyla konfiguracni polozka nalezena
	 */
	public Integer getInteger(ConfigurationKey key) {
		return getObject(key, Integer.class);
	}

	public Integer getInteger(ConfigurationKey key, Integer defaultValue) {
		return getObject(key, Integer.class, defaultValue);
	}

	public Integer getInteger(String key) {
		return !Checker.isBlank(key) ? getInteger(new VirtualConfigurationKey(key)) : null;
	}

	/**
	 * Vrati hodnotu konfiguracni polozky dle zadaneho klice
	 *
	 * @param key klic konfiguracni polozky
	 * @return hodnota konfiguracni polozky
	 * @throws cz.datalite.exception.ProblemException pokud nebyla konfiguracni polozka nalezena
	 */
	public Long getLong(ConfigurationKey key) {
		return getObject(key, Long.class);
	}

	public Long getLong(ConfigurationKey key, Long defaultValue) {
		return getObject(key, Long.class, defaultValue);
	}

	/**
	 * Vrati hodnotu konfiguracni polozky dle zadaneho klice
	 *
	 * @param key klic konfiguracni polozky
	 * @return hodnota konfiguracni polozky
	 * @throws cz.datalite.exception.ProblemException pokud nebyla konfiguracni polozka nalezena
	 */
	public Boolean getBoolean(ConfigurationKey key) {
		return getObject(key, Boolean.class);
	}

	public Boolean getBoolean(ConfigurationKey key, Boolean defaultValue) {
		return getObject(key, Boolean.class, defaultValue);
	}

	/**
	 * Vrati hodnotu konfiguracni polozky dle zadaneho klice
	 *
	 * @param key klic konfiguracni polozky
	 * @return hodnota konfiguracni polozky
	 * @throws cz.datalite.exception.ProblemException pokud nebyla konfiguracni polozka nalezena
	 */
	public Date getDate(ConfigurationKey key) {
		return getObject(key, Date.class);
	}

	public Date getDate(ConfigurationKey key, Date defaultValue) {
		return getObject(key, Date.class, defaultValue);
	}

	/**
	 * Kdy byla konfigurace naposledy obnovena
	 *
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate == null ? null : (Date) lastUpdate.clone();
	}

	/**
	 * Zaaktualizuje konfiguraci ze vsech konfiguracnich zdroju
	 */
	public void update() {

		logger.info("Update config sources");

		lastUpdate = DateTimeUtil.now();

		for (ConfigurationSource source : sources) {
			source.update();
		}
	}

	/**
	 * @return souhrnne informace o konfiguraci
	 */
	public String getSummary() {

		StringBuilder builder = new StringBuilder("Configuration [");
		builder.append("lastUpdate: ").append(lastUpdate);
		builder.append(", sources: ").append(sources.size());
		builder.append("]\n\n");
		for (ConfigurationSource source : sources) {
			Map<String, Object> items = source.getItems();
			builder.append("Source [impl: ").append(source.getClass().getSimpleName());
			builder.append(", precedence: ").append(source.getPrecedence());
			builder.append(", items: ").append(items.size());
			builder.append("]:\n");
			for (Entry<String, Object> entry : items.entrySet()) {
				builder.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
			}
			builder.append("-----------------------------\n\n");
		}
		return builder.toString();
	}

}
