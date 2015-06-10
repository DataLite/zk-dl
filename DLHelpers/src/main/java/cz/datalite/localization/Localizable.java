package cz.datalite.localization;

/**
 * Interface pro vyctove typy na zurnalech a jinych. Zurnalovany objekt je odkopirovany od ziveho, vyctovy typy se musi odkopirovat take.
 * Aby se preklady nemusely definovat dvakrat, budou vzdy dva enumy z ziveho a zurnaloveho objektu vracet pro odpovidajici polozky stejny klic.
 *
 * <p>
 *     Note: Puvodne se jmenoval {@code JournalEnum}.
 * </p>
 * @author pmarek
 */
public interface Localizable {

	/**
	 * Metoda pro vraceni klice pro preklad.
	 * 
	 * @return klic ze souboru enums.properties
	 */
	public String getKey();
	
}
