package cz.datalite.concurrent;

/**
 * Generator unikatnich ciselnych hodnot (sekvence hodnot unikatnich v ramci instance).
 * 
 * @author mstastny
 */
public final class UniqueStamp {

	private long lastStamp = 0;

	/**
	 * @return cas v milisekundach (nemusi byt presny, musi byt unikatni ve svem JRE)
	 */
	public synchronized long getUniqueStamp() {

		// Ziskame aktualni casovou znacku (ms)
		long ts = System.currentTimeMillis();

		if (ts <= lastStamp)
			// Zvedame o jednu ms
			return ++lastStamp;

		lastStamp = ts;
		return ts;
	}

}
