package cz.datalite.config;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Zdroj konfigurace. Pokud zdroj neimplementuje nekterou z "get" metod tzn. nektery z {@link ConfigurationValueType},
 * musi vyhazovat vyjimku {@link UnsupportedOperationException}.
 * 
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public interface ConfigurationSource {

	/**
	 * @return prioritu daneho zdroje - cim vyssi cislo, tim vyssi priorita
	 */
	public int getPrecedence();

	/**
	 * @return mapu klicu a hodnot
	 */
	public Map<String, Object> getItems();

	/**
	 * Aktualizuje zdroj (pokud to nepodporuje, nedela nic - nevyhazuje {@link UnsupportedOperationException}).
	 */
	public void update();

	/**
	 * @param key
	 * @return {@link String} pro dany klic, nebo <code>null</code>, pokud zdroj tento klic neobsahuje. Pokud dojde k
	 *         chybe behem pretypovani/konverze, je vyhozena prislusna vyjimka
	 */
	public String getString(ConfigurationKey key);

	/**
	 * @param key
	 * @return pole {@link String} pro dany klic, nebo <code>null</code>, pokud zdroj tento klic neobsahuje
	 */
	public String[] getStringArray(ConfigurationKey key);

	/**
	 * @param key
	 * @return {@link BigDecimal} pro dany klic, nebo <code>null</code>, pokud zdroj tento klic neobsahuje. Pokud dojde
	 *         k chybe behem pretypovani/konverze, je vyhozena prislusna vyjimka
	 */
	public BigDecimal getBigDecimal(ConfigurationKey key);

	/**
	 * @param key
	 * @return {@link Integer} pro dany klic, nebo <code>null</code>, pokud zdroj tento klic neobsahuje. Pokud dojde k
	 *         chybe behem pretypovani/konverze, je vyhozena prislusna vyjimka
	 */
	public Integer getInteger(ConfigurationKey key);

	/**
	 * @param key
	 * @return {@link Long} pro dany klic, nebo <code>null</code>, pokud zdroj tento klic neobsahuje. Pokud dojde k
	 *         chybe behem pretypovani/konverze, je vyhozena prislusna vyjimka
	 */
	public Long getLong(ConfigurationKey key);

	/**
	 * @param key
	 * @return {@link Boolean} pro dany klic, nebo <code>null</code>, pokud zdroj tento klic neobsahuje. Pokud dojde k
	 *         chybe behem pretypovani/konverze, je vyhozena prislusna vyjimka
	 */
	public Boolean getBoolean(ConfigurationKey key);

	/**
	 * @param key
	 * @return {@link Date} pro dany klic, nebo <code>null</code>, pokud zdroj tento klic neobsahuje
	 */
	public Date getDate(ConfigurationKey key);

}
