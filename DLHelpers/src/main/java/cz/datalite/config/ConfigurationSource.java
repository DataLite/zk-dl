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
    int getPrecedence();

	/**
	 * @return mapu klicu a hodnot
	 */
    Map<String, Object> getItems();

	/**
	 * Aktualizuje zdroj (pokud to nepodporuje, nedela nic - nevyhazuje {@link UnsupportedOperationException}).
	 */
    void update();

	/**
	 * @return {@link String} pro dany klic, nebo {@code null}, pokud zdroj tento klic neobsahuje. Pokud dojde k
	 *         chybe behem pretypovani/konverze, je vyhozena prislusna vyjimka
	 */
    String getString(ConfigurationKey key);

	/**
	 * @return pole {@link String} pro dany klic, nebo {@code null}, pokud zdroj tento klic neobsahuje
	 */
    String[] getStringArray(ConfigurationKey key);

	/**
	 * @return {@link BigDecimal} pro dany klic, nebo {@code null}, pokud zdroj tento klic neobsahuje. Pokud dojde
	 *         k chybe behem pretypovani/konverze, je vyhozena prislusna vyjimka
	 */
    BigDecimal getBigDecimal(ConfigurationKey key);

	/**
	 * @return {@link Integer} pro dany klic, nebo {@code null}, pokud zdroj tento klic neobsahuje. Pokud dojde k
	 *         chybe behem pretypovani/konverze, je vyhozena prislusna vyjimka
	 */
    Integer getInteger(ConfigurationKey key);

	/**
	 * @return {@link Long} pro dany klic, nebo {@code null}, pokud zdroj tento klic neobsahuje. Pokud dojde k
	 *         chybe behem pretypovani/konverze, je vyhozena prislusna vyjimka
	 */
    Long getLong(ConfigurationKey key);

	/**
	 * @return {@link Boolean} pro dany klic, nebo {@code null}, pokud zdroj tento klic neobsahuje. Pokud dojde k
	 *         chybe behem pretypovani/konverze, je vyhozena prislusna vyjimka
	 */
    Boolean getBoolean(ConfigurationKey key);

	/**
	 * @return {@link Date} pro dany klic, nebo {@code null}, pokud zdroj tento klic neobsahuje
	 */
    Date getDate(ConfigurationKey key);

}
