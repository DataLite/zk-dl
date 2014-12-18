package cz.datalite.config;

import cz.datalite.time.DateTimeUtil;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Standardni nemenny zdroj konfigurace.
 * 
 * <p>
 * Tato konfigurace obsahuje polozky, ktere jsou <b>po startu aplikace nemenne</b>! Jako sklad konfiguracnich polozek slouzi
 * {@link CompositeConfiguration}. Pri zavedeni systemu se pomoci {@link ImmutableSource#addConfiguration(Configuration)} pridaji
 * konfigurace jednotlivych modulu.
 * </p>
 * 
 * <p>
 * Chovani pri vyhledavani konfiguracnich polozek - viz <a href="http://commons.apache.org/configuration/index.html">http://commons.apache.org/configuration/index.html</a>.
 * </p>
 * 
 * <p>
 * Pro konfiguracni polozky typu {@link ConfigurationValueType#DATETIME} se pouziva format pro {@link cz.datalite.time.DateTimeUtil#parseISODateTime(String)}.
 * </p>
 * 
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public class ImmutableSource
		implements ConfigurationSource {

	private int precedence = 0;

	private CompositeConfiguration compositeConfiguration = new CompositeConfiguration();

	/**
	 * 
	 */
	public ImmutableSource() {
		// Bez komentare
	}

	/**
	 * 
	 * @param precedence
	 */
	public ImmutableSource(int precedence) {
		super();
		this.precedence = precedence;
	}

	@Override
	public String getString(ConfigurationKey key) {
		return compositeConfiguration.getString(key.getValue());
	}

	@Override
	public String[] getStringArray(ConfigurationKey key) {
		// Explicitni kontrola, protoze commons config vraci prazdne pole, pokud neni polozka definovana
		return compositeConfiguration.getProperty(key.getValue()) != null ? compositeConfiguration.getStringArray(key.getValue()) : null;
	}

	@Override
	public BigDecimal getBigDecimal(ConfigurationKey key) {
		return compositeConfiguration.getBigDecimal(key.getValue());
	}

	@Override
	public Integer getInteger(ConfigurationKey key) {
		return compositeConfiguration.getInteger(key.getValue(), null);
	}

	@Override
	public Long getLong(ConfigurationKey key) {
		return compositeConfiguration.getLong(key.getValue(), null);
	}

	@Override
	public Boolean getBoolean(ConfigurationKey key) {
		return compositeConfiguration.getBoolean(key.getValue(), null);
	}

	@Override
	public Date getDate(ConfigurationKey key) {
		return DateTimeUtil.parseISODateTime(compositeConfiguration.getString(key.getValue())).getTime();
	}

	public void addConfiguration(Configuration configuration) {
		compositeConfiguration.addConfiguration(configuration);
	}

	@Override
	public int getPrecedence() {
		return precedence;
	}

	@Override
	public void update() {
		// Nic
	}

	@Override
	public Map<String, Object> getItems() {

		Map<String, Object> items = new HashMap<>();
		Iterator<String> keyIterator = compositeConfiguration.getKeys();

		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			items.put(key, compositeConfiguration.getString(key));
		}
		return items;
	}

}
