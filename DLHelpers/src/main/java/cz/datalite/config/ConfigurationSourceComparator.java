package cz.datalite.config;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator pro zdroje konfiguraci, ktery se ridi jejich prioritou - {@link ConfigurationSource#getPrecedence()}. Zdroj s nejvyssi
 * prioritou (nejvetsi cislo) je prvni v kolekci - tzn. zdroje 10,15,0 budou serazeny jako 15,10,0.
 * 
 * @author <a href="mailto:mkouba@itsys.cz">Martin Kouba</a>
 */
public class ConfigurationSourceComparator
		implements Comparator<ConfigurationSource>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(ConfigurationSource o1, ConfigurationSource o2) {

		int p1 = o1.getPrecedence();
		int p2 = o2.getPrecedence();

		if (p1 == p2)
			return 0;

		return p1 < p2 ? 1 : -1;
	}

}
